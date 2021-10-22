package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity.Companion.CONSTRAINT
import javax.inject.Inject

class InternalRecipeSelectionNavigationUseCase @Inject constructor(
    private val context: Context
) : BaseUseCase<ProcessEditViewModel.ConnectionConstraint, Unit>() {

    override suspend fun execute(params: ProcessEditViewModel.ConnectionConstraint) {
        context.startActivity(Intent(context, InternalRecipeSelectionActivity::class.java).apply {
            putExtra(CONSTRAINT, params)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
