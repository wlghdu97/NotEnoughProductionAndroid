package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.db.ProcessDatabase
import javax.inject.Inject

internal class ProcessRepoImpl @Inject constructor(
    private val db: ProcessDatabase,
    private val mapper: ProcessMapper
) : ProcessRepo {

    override fun getProcess(processId: String): LiveData<Process?> {
        val liveData = db.getProcessDao().getProcess(processId)
        return Transformations.map(liveData) { it?.let { mapper.map(it) } }
    }

    override fun getProcesses(): DataSource.Factory<Int, Process> {
        return db.getProcessDao().getProcessList().map { mapper.map(it) }
    }

    override fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: Element,
        reversed: Boolean
    ) {
        TODO("not implemented")
    }

    override fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: Element,
        reversed: Boolean
    ) {
        TODO("not implemented")
    }

    override fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: Element,
        consumed: Boolean
    ) {
        TODO("not implemented")
    }
}