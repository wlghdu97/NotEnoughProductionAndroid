package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.xhlab.nep.shared.domain.recipe.model.CraftingRecipeView
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView

data class RoomCraftingRecipeView(
    @ColumnInfo(name = "recipe_id")
    override val recipeId: Long,
    override val amount: Int
) : CraftingRecipeView() {
    @Ignore
    override var itemList: List<RecipeElementView> = emptyList()
    @Ignore
    override var resultItemList: List<RecipeElementView> = emptyList()
}