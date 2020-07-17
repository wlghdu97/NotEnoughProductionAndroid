package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary

interface ProcessRepo {
    fun getProcesses(): DataSource.Factory<Int, ProcessSummary>
    fun getProcessesByTarget(targetElementKey: String): DataSource.Factory<Int, ProcessSummary>
    suspend fun getProcess(processId: String): Process?
    suspend fun getProcessLiveData(processId: String): LiveData<Process?>
    suspend fun createProcess(name: String, targetRecipe: Recipe, keyElement: Element): Boolean
    suspend fun insertProcess(process: Process)
    suspend fun renameProcess(processId: String, name: String)
    suspend fun deleteProcess(processId: String)
    suspend fun exportProcessString(processId: String): String?
    suspend fun connectProcess(processId: String, fromProcessId: String, to: Recipe?, element: Element)
    suspend fun connectRecipe(processId: String, from: Recipe, to: Recipe?, element: Element, reversed: Boolean)
    suspend fun disconnectRecipe(processId: String, from: Recipe, to: Recipe, element: Element, reversed: Boolean)
    suspend fun markNotConsumed(processId: String, recipe: Recipe, element: Element, consumed: Boolean)
}