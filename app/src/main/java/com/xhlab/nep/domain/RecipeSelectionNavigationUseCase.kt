package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity.Companion.CONSTRAINT
import javax.inject.Inject

class RecipeSelectionNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<ProcessEditViewModel.ConnectionConstraint, Unit>() {

    override suspend fun execute(params: ProcessEditViewModel.ConnectionConstraint) {
        context.startActivity(Intent(context, RecipeSelectionActivity::class.java).apply {
            putExtra(CONSTRAINT, params)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
