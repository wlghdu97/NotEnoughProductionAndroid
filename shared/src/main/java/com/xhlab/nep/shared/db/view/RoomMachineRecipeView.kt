package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.xhlab.nep.shared.domain.recipe.model.MachineRecipeView
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView

data class RoomMachineRecipeView(
    @ColumnInfo(name = "recipe_id")
    override val recipeId: Long,
    @ColumnInfo(name = "enabled")
    override val isEnabled: Boolean,
    override val duration: Int,
    @ColumnInfo(name = "power_type")
    override val powerType: Int,
    override val ept: Int,
    @ColumnInfo(name = "machine_id")
    override val machineId: Int,
    @ColumnInfo(name = "machine_name")
    override val machineName: String
) : MachineRecipeView() {
    @Ignore
    override var itemList: List<RecipeElementView> = emptyList()
    @Ignore
    override var resultItemList: List<RecipeElementView> = emptyList()
}