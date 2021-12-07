package com.xhlab.nep.shared.data.process

import co.touchlab.stately.isolate.IsolateState
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.NepProcess
import com.xhlab.nep.shared.db.ProcessQueries
import com.xhlab.nep.shared.db.createOffsetLimitPager
import com.xhlab.nep.shared.util.UUID
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import com.xhlab.nep.shared.db.Process as ProcessEntity

class ProcessRepoImpl constructor(
    private val db: NepProcess,
    private val io: CoroutineDispatcher,
    json: Json
) : ProcessRepo {

    private val mapper = ProcessMapper(json)
    private val roomMapper = SqlDelightProcessMapper(json)

    // TODO: replace with LruCache
    private val cache = IsolateState { mutableMapOf<String, Process>() }

    override suspend fun getProcess(processId: String): Process? = withContext(io) {
        val process = db.processQueries.getProcess(processId).executeAsOneOrNull()
        if (process != null) {
            mapper.map(process)
        } else null
    }

    override suspend fun getProcessFlow(processId: String) = withContext(io) {
        db.processQueries.getProcess(processId, processMapper).asFlow().mapToOneOrNull()
    }

    override fun getProcesses(): Pager<Int, ProcessSummary> {
        return with(db.processSummaryQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getProcesses(limit, offset, processSummaryMapper)
                },
                countQuery = getProcessCount(),
                transactor = this
            )
        }
    }

    override fun getProcessesByTarget(targetElementKey: String): Pager<Int, ProcessSummary> {
        return with(db.processSummaryQueries) {
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    getProcessesByTarget(targetElementKey, limit, offset, processSummaryMapper)
                },
                countQuery = getProcessCountByTarget(targetElementKey),
                transactor = this
            )
        }
    }

    override suspend fun createProcess(
        name: String,
        targetRecipe: Recipe,
        keyElement: RecipeElement
    ): Boolean {
        val processId = UUID.generateLongUUID().toString()
        val process = Process(processId, name, targetRecipe, keyElement)
        db.processQueries.insert(roomMapper.map(process))
        return db.processQueries.getLastProcessId().executeAsOne() == processId
    }

    override suspend fun insertProcess(process: Process) {
        db.processQueries.insert(roomMapper.mapWithRandomId(process))
    }

    override suspend fun renameProcess(processId: String, name: String) = withContext(io) {
        cache.access {
            val process = it.getProcessInternal(processId)
            if (process != null) {
                process.name = name.trim()
                db.processQueries.update(process)
            } else {
                throw ProcessNotFoundException()
            }
        }
    }

    override suspend fun deleteProcess(processId: String) = withContext(io) {
        db.processQueries.delete(processId)
    }

    override suspend fun exportProcessString(processId: String) = withContext(io) {
        db.processQueries.getProcessJson(processId).executeAsOne()
    }

    override suspend fun connectProcess(
        processId: String,
        fromProcessId: String,
        to: Recipe?,
        element: RecipeElement
    ) {
        cache.access {
            val process = it.getProcessInternal(processId)
            val subProcess = it.getProcessInternal(fromProcessId)
            if (process != null && subProcess != null) {
                process.connectProcess(subProcess, to, element)
                db.processQueries.update(process)
            } else {
                throw ProcessNotFoundException()
            }
        }
    }

    override suspend fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: RecipeElement,
        reversed: Boolean
    ) = withContext(io) {
        cache.access {
            val process = it.getProcessInternal(processId)
            if (process != null) {
                process.connectRecipe(from, to, element, reversed)
                db.processQueries.update(process)
            } else {
                throw ProcessNotFoundException()
            }
        }
    }

    override suspend fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: RecipeElement,
        reversed: Boolean
    ) = withContext(io) {
        cache.access {
            val process = it.getProcessInternal(processId)
            if (process != null) {
                process.disconnectRecipe(from, to, element, reversed)
                db.processQueries.update(process)
            } else {
                throw ProcessNotFoundException()
            }
        }
    }

    override suspend fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: RecipeElement,
        consumed: Boolean
    ) = withContext(io) {
        cache.access {
            val process = it.getProcessInternal(processId)
            if (process != null) {
                process.markNotConsumed(recipe, element, consumed)
                db.processQueries.update(process)
            } else {
                throw ProcessNotFoundException()
            }
        }
    }

    private fun MutableMap<String, Process>.getProcessInternal(processId: String): Process? {
        val cached = this[processId]
        return if (cached != null) {
            cached
        } else {
            val process = db.processQueries
                .getProcess(processId, processMapper)
                .executeAsOneOrNull()
            if (process != null) {
                updateProcessCache(processId, process)
            }
            process
        }
    }

    private fun MutableMap<String, Process>.updateProcessCache(
        processId: String,
        process: Process
    ) {
        this[processId] = process
    }

    private fun ProcessQueries.update(process: Process) {
        with(roomMapper.map(process)) {
            update(name, unlocalized_name, localized_name, amount, node_count, json, process_id)
        }
        cache.access {
            it.updateProcessCache(process.id, process)
        }
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

    private val processMapper = { process_id: String,
                                  name: String,
                                  unlocalized_name: String,
                                  localized_name: String,
                                  amount: Int,
                                  node_count: Int,
                                  json: String ->
        mapper.map(
            ProcessEntity(
                process_id = process_id,
                name = name,
                unlocalized_name = unlocalized_name,
                localized_name = localized_name,
                amount = amount,
                node_count = node_count,
                json = json
            )
        )
    }

    private val processSummaryMapper = { process_id: String,
                                         name: String,
                                         unlocalized_name: String,
                                         localized_name: String,
                                         amount: Int,
                                         node_count: Int ->
        ProcessSummaryImpl(
            processId = process_id,
            name = name,
            unlocalizedName = unlocalized_name,
            localizedName = localized_name,
            amount = amount,
            nodeCount = node_count
        )
    }

    data class ProcessSummaryImpl(
        override val processId: String,
        override val name: String,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val amount: Int,
        override val nodeCount: Int
    ) : ProcessSummary()

    class ProcessNotFoundException : NullPointerException("process not found.")

    companion object {
        private const val PAGE_SIZE = 5
    }
}
