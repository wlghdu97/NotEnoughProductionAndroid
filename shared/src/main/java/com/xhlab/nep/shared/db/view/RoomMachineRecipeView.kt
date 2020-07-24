package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeElementView

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
    override val itemList: ArrayList<RecipeElementView> = arrayListOf()
    @Ignore
    override val resultItemList: ArrayList<RecipeElementView> = arrayListOf()
}