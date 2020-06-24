package com.xhlab.nep.shared.domain.recipe.model

abstract class CraftingRecipeView : RecipeView() {
    abstract override val recipeId: Long
    abstract override val itemList: List<RecipeElementView>
    abstract override val resultItemList: List<RecipeElementView>
}