package com.xhlab.nep.model.recipes.view

import kotlinx.serialization.Serializable

@Serializable
data class RecipeMachineView(
    val machineId: Int,
    val machineName: String,
    val modName: String,
    val recipeCount: Int
)
