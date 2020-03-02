package com.xhlab.nep.shared.domain.recipe.model

abstract class GregtechRecipeView : RecipeView() {
    abstract override val recipeId: Long
    abstract override val amount: Int
    abstract override val itemList: List<RecipeElementView>
    abstract override val resultItemList: List<RecipeElementView>
    abstract val isEnabled: Boolean
    abstract val duration: Int
    abstract val eut: Int
    abstract val machineId: Int
    abstract val machineName: String
}