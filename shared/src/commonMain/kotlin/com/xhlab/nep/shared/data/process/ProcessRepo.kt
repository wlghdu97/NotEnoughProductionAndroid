package com.xhlab.nep.shared.data.process

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import kotlinx.coroutines.flow.Flow

interface ProcessRepo {
    fun getProcesses(): Pager<Int, ProcessSummary>
    fun getProcessesByTarget(targetElementKey: String): Pager<Int, ProcessSummary>
    suspend fun getProcess(processId: String): Process?
    suspend fun getProcessFlow(processId: String): Flow<Process?>
    suspend fun createProcess(
        name: String,
        targetRecipe: Recipe,
        keyElement: RecipeElement
    ): Boolean

    suspend fun insertProcess(process: Process)
    suspend fun renameProcess(processId: String, name: String)
    suspend fun deleteProcess(processId: String)
    suspend fun exportProcessString(processId: String): String?
    suspend fun connectProcess(
        processId: String,
        fromProcessId: String,
        to: Recipe?,
        element: RecipeElement
    )

    suspend fun connectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe?,
        element: RecipeElement,
        reversed: Boolean
    )

    suspend fun disconnectRecipe(
        processId: String,
        from: Recipe,
        to: Recipe,
        element: RecipeElement,
        reversed: Boolean
    )

    suspend fun markNotConsumed(
        processId: String,
        recipe: Recipe,
        element: RecipeElement,
        consumed: Boolean
    )
}
