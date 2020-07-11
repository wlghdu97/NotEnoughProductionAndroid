package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.CONNECT_TO_PARENT
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.ELEMENT_KEY
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.ELEMENT_TYPE
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.PROCESS_ID
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.RECIPE
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.RECIPE_DEGREE
import javax.inject.Inject

class RecipeSelectionNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<RecipeSelectionNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, RecipeSelectionActivity::class.java).apply {
            putExtra(PROCESS_ID, params.processId)
            putExtra(CONNECT_TO_PARENT, params.connectToParent)
            putExtra(RECIPE, params.recipe)
            putExtra(RECIPE_DEGREE, params.degree)
            putExtra(ELEMENT_KEY, params.elementKey)
            putExtra(ELEMENT_TYPE, params.elementType)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(
        val processId: String,
        val connectToParent: Boolean,
        val recipe: Recipe,
        val degree: Int,
        val elementKey: String,
        val elementType: Int
    )
}