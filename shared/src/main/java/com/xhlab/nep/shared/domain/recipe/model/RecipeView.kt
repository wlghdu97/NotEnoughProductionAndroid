package com.xhlab.nep.shared.domain.recipe.model

abstract class RecipeView {
    abstract val recipeId: Long
    abstract val itemList: List<RecipeElementView>
    abstract val resultItemList: List<RecipeElementView>
}