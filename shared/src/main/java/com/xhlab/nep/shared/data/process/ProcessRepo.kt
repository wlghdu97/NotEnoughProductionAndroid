package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process

interface ProcessRepo {
    fun getProcesses(): DataSource.Factory<Int, Process>
    suspend fun getProcess(processId: String): LiveData<Process?>
    suspend fun createProcess(name: String, targetRecipe: Recipe, keyElement: Element): Boolean
    suspend fun connectRecipe(processId: String, from: Recipe, to: Recipe?, element: Element, reversed: Boolean)
    suspend fun disconnectRecipe(processId: String, from: Recipe, to: Recipe, element: Element, reversed: Boolean)
    suspend fun markNotConsumed(processId: String, recipe: Recipe, element: Element, consumed: Boolean)
}