package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.process.calculator.ProcessCalculationActivity
import com.xhlab.nep.ui.process.calculator.ProcessCalculationActivity.Companion.PROCESS_ID
import javax.inject.Inject

class ProcessCalculationNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<ProcessCalculationNavigationUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) {
        context.startActivity(Intent(context, ProcessCalculationActivity::class.java).apply {
            putExtra(PROCESS_ID, params.processId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameter(val processId: String)
}