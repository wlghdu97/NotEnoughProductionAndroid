package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.ui.recipe.MachineRecipeListActivity
import com.xhlab.nep.ui.recipe.MachineRecipeListFragment
import javax.inject.Inject

class MachineRecipeListNavigationUseCase @Inject constructor(
    private val context: Context
) : BaseUseCase<MachineRecipeListNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, MachineRecipeListActivity::class.java).apply {
            putExtras(MachineRecipeListFragment.getBundle(params))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(val elementId: Long, val machineId: Int?)
}
