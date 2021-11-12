package com.xhlab.nep.model.recipes.view

import com.xhlab.nep.model.RecipeElement

abstract class CraftingRecipeView : RecipeView() {
    abstract override val recipeId: Long
    abstract override val itemList: List<RecipeElement>
    abstract override val resultItemList: List<RecipeElement>
}
