package com.xhlab.nep.shared.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.ListDataSource
import com.xhlab.test_shared.ProcessData
import javax.inject.Inject

class FakeProcessRepo @Inject constructor() : ProcessRepo {

    private val processGlass = MutableLiveData(ProcessData.processGlass)
    private val processPE = MutableLiveData(ProcessData.processPE)

    override fun getProcess(processId: String): LiveData<Process?> {
        return when (processId) {
            processGlass.value?.id -> processGlass
            processPE.value?.id -> processPE
            else -> MutableLiveData(null)
        }
    }

    override fun getProcesses(): DataSource.Factory<Int, Process> {
        return ListDataSource(ProcessData.processList)
    }

    override fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: Element,
        reversed: Boolean
    ) {
        val process = getProcessMutable(processId)
        process?.value?.let {
            val result = it.connectRecipe(from, to, element, reversed)
            if (result) {
                process.postValue(it)
            }
        }
    }

    override fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: Element,
        reversed: Boolean
    ) {
        val process = getProcessMutable(processId)
        process?.value?.let {
            val result = it.disconnectRecipe(from, to, element, reversed)
            if (result) {
                process.postValue(it)
            }
        }
    }

    override fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: Element,
        consumed: Boolean
    ) {
        val process = getProcessMutable(processId)
        process?.value?.let {
            val result = it.markNotConsumed(recipe, element, consumed)
            if (result) {
                process.postValue(it)
            }
        }
    }

    private fun getProcessMutable(processId: String): MutableLiveData<Process>? = when (processId) {
        processGlass.value?.id -> processGlass
        processPE.value?.id -> processPE
        else -> null
    }
}