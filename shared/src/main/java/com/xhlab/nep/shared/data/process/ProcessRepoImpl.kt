package com.xhlab.nep.shared.data.process

import android.util.LruCache
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.db.ProcessDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

internal class ProcessRepoImpl @Inject constructor(
    private val db: ProcessDatabase,
    private val mapper: ProcessMapper,
    private val roomMapper: RoomProcessMapper
) : ProcessRepo {

    private val io = Dispatchers.IO
    private val cache = LruCache<String, MutableLiveData<Process?>>(10)

    override suspend fun getProcess(processId: String): LiveData<Process?> = withContext(io) {
        getProcessInternal(processId)
    }

    override fun getProcesses(): DataSource.Factory<Int, ProcessSummary> {
        return db.getProcessDao().getProcessList().map { it as ProcessSummary }
    }

    override suspend fun createProcess(name: String, targetRecipe: Recipe, keyElement: Element): Boolean {
        val processId = UUID.randomUUID().toString()
        val process = Process(processId, name, targetRecipe, keyElement)
        val result = db.getProcessDao().insert(roomMapper.map(process))
        return result != -1L
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
            db.getProcessDao().upsert(roomMapper.map(process))
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
            db.getProcessDao().upsert(roomMapper.map(process))
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
            db.getProcessDao().upsert(roomMapper.map(process))
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
            val process = db.getProcessDao().getProcess(processId)
            if (process != null) {
                val liveData = MutableLiveData(mapper.map(process))
                cache.put(processId, liveData)
                liveData
            } else {
                MutableLiveData<Process?>(null)
            }
        }
    }

    class ProcessNotFoundException : NullPointerException("process not found.")
}