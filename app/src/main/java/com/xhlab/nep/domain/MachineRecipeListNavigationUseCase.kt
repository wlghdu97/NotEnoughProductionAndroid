package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.recipe.MachineRecipeListActivity
import com.xhlab.nep.ui.recipe.MachineRecipeListActivity.Companion.ELEMENT_ID
import com.xhlab.nep.ui.recipe.MachineRecipeListActivity.Companion.MACHINE_ID
import javax.inject.Inject

class MachineRecipeListNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<MachineRecipeListNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, MachineRecipeListActivity::class.java).apply {
            putExtra(ELEMENT_ID, params.elementId)
            putExtra(MACHINE_ID, params.machineId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(val elementId: Long, val machineId: Int?)
}