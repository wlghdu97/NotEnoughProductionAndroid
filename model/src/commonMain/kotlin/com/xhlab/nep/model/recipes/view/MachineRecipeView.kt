package com.xhlab.nep.model.recipes.view

import com.xhlab.nep.model.RecipeElement

abstract class MachineRecipeView : RecipeView() {
    abstract override val recipeId: Long
    abstract override val itemList: List<RecipeElement>
    abstract override val resultItemList: List<RecipeElement>
    abstract val isEnabled: Boolean
    abstract val duration: Int
    abstract val powerType: Int
    abstract val ept: Int
    abstract val machineId: Int
    abstract val machineName: String

    companion object {
        enum class PowerType(val type: Int) {
            NONE(-1), EU(0), RF(1), FUEL(2)
        }
    }
}
