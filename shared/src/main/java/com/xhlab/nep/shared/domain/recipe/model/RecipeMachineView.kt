package com.xhlab.nep.shared.domain.recipe.model

data class RecipeMachineView(
    val machineId: Int,
    val machineName: String,
    val modName: String,
    val recipeCount: Int
)