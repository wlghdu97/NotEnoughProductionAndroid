package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.RecipeElementView

data class RoomCraftingRecipeView(
    @ColumnInfo(name = "recipe_id")
    override val recipeId: Long
) : CraftingRecipeView() {
    @Ignore
    override var itemList: List<RecipeElementView> = emptyList()
    @Ignore
    override var resultItemList: List<RecipeElementView> = emptyList()
}