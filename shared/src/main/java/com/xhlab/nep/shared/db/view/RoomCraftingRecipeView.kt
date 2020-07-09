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
    override val itemList: ArrayList<RecipeElementView> = arrayListOf()
    @Ignore
    override val resultItemList: ArrayList<RecipeElementView> = arrayListOf()
}