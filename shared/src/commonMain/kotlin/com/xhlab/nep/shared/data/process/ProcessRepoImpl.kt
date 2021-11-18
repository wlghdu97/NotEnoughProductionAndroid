package com.xhlab.nep.shared.data.process

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
import com.xhlab.nep.shared.db.Process as ProcessEntity

internal class ProcessRepoImpl constructor(
    private val db: NepProcess,
    private val io: CoroutineDispatcher
) : ProcessRepo {

    private val mapper = ProcessMapper()
    private val roomMapper = SqlDelightProcessMapper()

    // TODO: replace with LruCache
    private val cache = mutableMapOf<String, Process>()

    override suspend fun getProcess(processId: String): Process? = withContext(io) {
        val process = db.processQueries.getProcess(processId).executeAsOneOrNull()
        if (process != null) {
            mapper.map(process)
        } else null
    }

    override suspend fun getProcessFlow(processId: String) = withContext(io) {
        db.processQueries.getProcess(processId, processCacheMapper).asFlow().mapToOneOrNull()
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
        db.processQueries.insert(roomMapper.map(process))
    }

    override suspend fun renameProcess(processId: String, name: String) = withContext(io) {
        val process = getProcessInternal(processId)
        if (process != null) {
            process.name = name.trim()
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
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
        val process = getProcessInternal(processId)
        val subProcess = getProcess(fromProcessId)
        if (process != null && subProcess != null) {
            process.connectProcess(subProcess, to, element)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: RecipeElement,
        reversed: Boolean
    ) = withContext(io) {
        val process = getProcessInternal(processId)
        if (process != null) {
            process.connectRecipe(from, to, element, reversed)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: RecipeElement,
        reversed: Boolean
    ) = withContext(io) {
        val process = getProcessInternal(processId)
        if (process != null) {
            process.disconnectRecipe(from, to, element, reversed)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: RecipeElement,
        consumed: Boolean
    ) = withContext(io) {
        val process = getProcessInternal(processId)
        if (process != null) {
            process.markNotConsumed(recipe, element, consumed)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    private suspend fun getProcessInternal(
        processId: String
    ): Process? = withContext(io) {
        cache.get(processId) ?: db.processQueries.getProcess(processId, processCacheMapper)
            .executeAsOneOrNull()
    }

    private fun ProcessQueries.upsert(process: ProcessEntity) = with(process) {
        upsert(name, unlocalized_name, localized_name, amount, node_count, json, process_id)
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

    private val processCacheMapper = { process_id: String,
                                       name: String,
                                       unlocalized_name: String,
                                       localized_name: String,
                                       amount: Int,
                                       node_count: Int,
                                       json: String ->
        val process = mapper.map(
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
        cache.put(process_id, process)
        process
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
