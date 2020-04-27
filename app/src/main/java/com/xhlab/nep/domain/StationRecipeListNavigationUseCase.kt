package com.xhlab.nep.domain

import android.content.Context
import android.content.Intent
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.ui.recipe.StationRecipeListActivity
import com.xhlab.nep.ui.recipe.StationRecipeListActivity.Companion.ELEMENT_ID
import com.xhlab.nep.ui.recipe.StationRecipeListActivity.Companion.STATION_ID
import javax.inject.Inject

class StationRecipeListNavigationUseCase @Inject constructor(
    private val context: Context
) : UseCase<StationRecipeListNavigationUseCase.Parameters, Unit>() {

    override suspend fun execute(params: Parameters) {
        context.startActivity(Intent(context, StationRecipeListActivity::class.java).apply {
            putExtra(ELEMENT_ID, params.elementId)
            putExtra(STATION_ID, params.stationId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    data class Parameters(val elementId: Long, val stationId: Int?)
}