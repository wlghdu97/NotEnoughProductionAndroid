package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary

interface ProcessRepo {
    fun getProcesses(): DataSource.Factory<Int, ProcessSummary>
    suspend fun getProcess(processId: String): LiveData<Process?>
    suspend fun createProcess(name: String, targetRecipe: Recipe, keyElement: Element): Boolean
    suspend fun renameProcess(processId: String, name: String)
    suspend fun deleteProcess(processId: String)
    suspend fun connectRecipe(processId: String, from: Recipe, to: Recipe?, element: Element, reversed: Boolean)
    suspend fun disconnectRecipe(processId: String, from: Recipe, to: Recipe, element: Element, reversed: Boolean)
    suspend fun markNotConsumed(processId: String, recipe: Recipe, element: Element, consumed: Boolean)
}