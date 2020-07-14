package com.xhlab.nep.shared.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.ListDataSource
import com.xhlab.test_shared.ProcessData
import javax.inject.Inject

class FakeProcessRepo @Inject constructor() : ProcessRepo {

    private val processGlass = MutableLiveData(ProcessData.processGlass)
    private val processPE = MutableLiveData(ProcessData.processPE)
    private val processChest = MutableLiveData(ProcessData.processChest)
    private val processPlasticSheet = MutableLiveData(ProcessData.processPlasticSheet)

    override suspend fun getProcess(processId: String): Process? {
        return when (processId) {
            processGlass.value?.id -> ProcessData.processGlass
            processPE.value?.id -> ProcessData.processPE
            processChest.value?.id -> ProcessData.processChest
            processPlasticSheet.value?.id -> ProcessData.processPlasticSheet
            else -> null
        }
    }

    override suspend fun getProcessLiveData(processId: String): LiveData<Process?> {
        return when (processId) {
            processGlass.value?.id -> processGlass
            processPE.value?.id -> processPE
            processChest.value?.id -> processChest
            processPlasticSheet.value?.id -> processPlasticSheet
            else -> MutableLiveData(null)
        }
    }

    override fun getProcesses(): DataSource.Factory<Int, ProcessSummary> {
        return ListDataSource(ProcessData.processList)
    }

    override suspend fun createProcess(
        name: String,
        targetRecipe: Recipe,
        keyElement: Element
    ): Boolean {
        TODO("not implemented")
    }

    override suspend fun insertProcess(process: Process) {
        TODO("not implemented")
    }

    override suspend fun renameProcess(processId: String, name: String) {
        TODO("not implemented")
    }

    override suspend fun deleteProcess(processId: String) {
        TODO("not implemented")
    }

    override suspend fun exportProcessString(processId: String): String {
        TODO("not implemented")
    }

    override suspend fun connectRecipe(
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
            } else {
                throw RuntimeException("failed to connect recipe")
            }
        }
    }

    override suspend fun disconnectRecipe(
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

    override suspend fun markNotConsumed(
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