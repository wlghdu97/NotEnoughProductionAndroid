package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity.Companion.MACHINE_ID
import javax.inject.Inject

class MachineResultNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<MachineResultNavigationUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) {
        context.startActivity(Intent(context, MachineResultActivity::class.java).apply {
            putExtra(MACHINE_ID, params.machineId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameter(val machineId: Int)
}