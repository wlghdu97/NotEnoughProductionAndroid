package com.xhlab.nep.shared.data.process

import android.util.LruCache
import androidx.lifecycle.MutableLiveData
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.NepProcess
import com.xhlab.nep.shared.db.ProcessQueries
import com.xhlab.nep.shared.db.createOffsetLimitPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import com.xhlab.nep.shared.db.Process as ProcessEntity

internal class ProcessRepoImpl @Inject constructor(
    private val db: NepProcess,
    private val mapper: ProcessMapper,
    private val roomMapper: SqlDelightProcessMapper
) : ProcessRepo {

    private val io = Dispatchers.IO
    private val cache = LruCache<String, MutableLiveData<Process?>>(10)

    override suspend fun getProcess(processId: String): Process? = withContext(io) {
        val process = db.processQueries.getProcess(processId).executeAsOneOrNull()
        if (process != null) {
            mapper.map(process)
        } else null
    }

    override suspend fun getProcessLiveData(processId: String) = withContext(io) {
        getProcessInternal(processId)
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
        keyElement: Element
    ): Boolean {
        val processId = UUID.randomUUID().toString()
        val process = Process(processId, name, targetRecipe, keyElement)
        db.processQueries.insert(roomMapper.map(process))
        return db.processQueries.getLastProcessId().executeAsOne() == processId
    }

    override suspend fun insertProcess(process: Process) {
        db.processQueries.insert(roomMapper.map(process))
    }

    override suspend fun renameProcess(processId: String, name: String) = withContext(io) {
        val liveData = getProcessInternal(processId)
        val process = liveData.value
        if (process != null) {
            process.name = name.trim()
            liveData.postValue(process)
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
        element: Element
    ) {
        val liveData = getProcessInternal(processId)
        val process = liveData.value
        val subProcess = getProcess(fromProcessId)
        if (process != null && subProcess != null) {
            process.connectProcess(subProcess, to, element)
            liveData.postValue(process)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: Element,
        reversed: Boolean
    ) = withContext(io) {
        val liveData = getProcessInternal(processId)
        val process = liveData.value
        if (process != null) {
            process.connectRecipe(from, to, element, reversed)
            liveData.postValue(process)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: Element,
        reversed: Boolean
    ) = withContext(io) {
        val liveData = getProcessInternal(processId)
        val process = liveData.value
        if (process != null) {
            process.disconnectRecipe(from, to, element, reversed)
            liveData.postValue(process)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    override suspend fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: Element,
        consumed: Boolean
    ) = withContext(io) {
        val liveData = getProcessInternal(processId)
        val process = liveData.value
        if (process != null) {
            process.markNotConsumed(recipe, element, consumed)
            liveData.postValue(process)
            db.processQueries.upsert(roomMapper.map(process))
        } else {
            throw ProcessNotFoundException()
        }
    }

    private suspend fun getProcessInternal(
        processId: String
    ): MutableLiveData<Process?> = withContext(io) {
        val cached = cache.get(processId)
        if (cached != null) {
            cached
        } else {
            val process = db.processQueries.getProcess(processId).executeAsOneOrNull()
            if (process != null) {
                val liveData = MutableLiveData(mapper.map(process))
                cache.put(processId, liveData)
                liveData
            } else {
                MutableLiveData<Process?>(null)
            }
        }
    }

    private fun ProcessQueries.upsert(process: ProcessEntity) = with(process) {
        upsert(name, unlocalized_name, localized_name, amount, node_count, json, process_id)
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

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
