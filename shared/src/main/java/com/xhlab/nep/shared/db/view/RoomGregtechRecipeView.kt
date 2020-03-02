package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.xhlab.nep.shared.domain.recipe.model.GregtechRecipeView
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView

data class RoomGregtechRecipeView(
    @ColumnInfo(name = "recipe_id")
    override val recipeId: Long,
    override val amount: Int,
    @ColumnInfo(name = "enabled")
    override val isEnabled: Boolean,
    override val duration: Int,
    override val eut: Int,
    @ColumnInfo(name = "machine_id")
    override val machineId: Int,
    @ColumnInfo(name = "machine_name")
    override val machineName: String
) : GregtechRecipeView() {
    @Ignore
    override var itemList: List<RecipeElementView> = emptyList()
    @Ignore
    override var resultItemList: List<RecipeElementView> = emptyList()
}