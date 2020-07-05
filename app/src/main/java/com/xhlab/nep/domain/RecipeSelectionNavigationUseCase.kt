package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.ELEMENT_KEY
import javax.inject.Inject

class RecipeSelectionNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<RecipeSelectionNavigationUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) {
        context.startActivity(Intent(context, RecipeSelectionActivity::class.java).apply {
            putExtra(ELEMENT_KEY, params.elementKey)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameter(val elementKey: String)
}