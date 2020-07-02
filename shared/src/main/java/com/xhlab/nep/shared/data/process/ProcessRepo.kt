package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process

interface ProcessRepo {
    fun getProcess(processId: String): LiveData<Process?>
    fun getProcesses(): DataSource.Factory<Int, Process>
    fun connectRecipe(processId: String, from: Recipe, to: Recipe, element: Element, reversed: Boolean)
    fun disconnectRecipe(processId: String, from: Recipe, to: Recipe, element: Element, reversed: Boolean)
    fun markNotConsumed(processId: String, recipe: Recipe, element: Element, consumed: Boolean)
}