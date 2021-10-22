package com.xhlab.nep.shared.data

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.multiplatform.paging.PagingResult
import com.xhlab.multiplatform.paging.createPager
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.test.shared.ProcessData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class FakeProcessRepo @Inject constructor() : ProcessRepo {

    private val processGlass = MutableStateFlow(ProcessData.processGlass)
    private val processPE = MutableStateFlow(ProcessData.processPE)
    private val processChest = MutableStateFlow(ProcessData.processChest)
    private val processPlasticSheet = MutableStateFlow(ProcessData.processPlasticSheet)

    override suspend fun getProcess(processId: String): Process? {
        return when (processId) {
            processGlass.value.id -> ProcessData.processGlass
            processPE.value.id -> ProcessData.processPE
            processChest.value.id -> ProcessData.processChest
            processPlasticSheet.value.id -> ProcessData.processPlasticSheet
            else -> null
        }
    }

    override suspend fun getProcessFlow(processId: String): Flow<Process?> {
        return when (processId) {
            processGlass.value.id -> processGlass
            processPE.value.id -> processPE
            processChest.value.id -> processChest
            processPlasticSheet.value.id -> processPlasticSheet
            else -> MutableStateFlow(null)
        }
    }

    override fun getProcesses(): Pager<Int, ProcessSummary> {
        return createPager(
            clientScope = pagerScope,
            config = PagingConfig(5),
            initialKey = 0,
            getItems = { key, _ ->
                PagingResult(
                    items = ProcessData.processSummaryList,
                    currentKey = key,
                    prevKey = { null },
                    nextKey = { null }
                )
            }
        )
    }

    override fun getProcessesByTarget(targetElementKey: String): Pager<Int, ProcessSummary> {
        TODO("not implemented")
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

    override suspend fun connectProcess(
        processId: String,
        fromProcessId: String,
        to: Recipe?,
        element: Element
    ) {
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
                process.value = it
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
                process.value = it
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
                process.value = it
            }
        }
    }

    private fun getProcessMutable(processId: String): MutableStateFlow<Process>? =
        when (processId) {
            processGlass.value.id -> processGlass
            processPE.value.id -> processPE
            processChest.value.id -> processChest
            processPlasticSheet.value.id -> processPlasticSheet
            else -> null
        }
}
