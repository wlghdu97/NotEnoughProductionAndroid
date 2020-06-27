package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.editor.ProcessEditActivity
import com.xhlab.nep.ui.process.editor.ProcessEditActivity.Companion.PROCESS_ID
import javax.inject.Inject

class ProcessEditNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<ProcessEditNavigationUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) {
        context.startActivity(Intent(context, ProcessEditActivity::class.java).apply {
            putExtra(PROCESS_ID, params.processId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameter(val processId: String)
}