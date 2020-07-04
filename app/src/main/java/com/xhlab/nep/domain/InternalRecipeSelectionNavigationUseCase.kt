package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.CONNECT_TO_PARENT
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.ELEMENT_KEY
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.PROCESS_ID
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.RECIPE_DEGREE
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.RECIPE
import javax.inject.Inject

class InternalRecipeSelectionNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<InternalRecipeSelectionNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, InternalRecipeSelectionActivity::class.java).apply {
            putExtra(PROCESS_ID, params.processId)
            putExtra(CONNECT_TO_PARENT, params.connectToParent)
            putExtra(RECIPE, params.recipe)
            putExtra(RECIPE_DEGREE, params.degree)
            putExtra(ELEMENT_KEY, params.elementKey)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(
        val processId: String,
        val connectToParent: Boolean,
        val recipe: Recipe,
        val degree: Int,
        val elementKey: String
    )
}