package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView

data class RoomRecipeElementView(
    override val id: Long,
    @ColumnInfo(name = "localized_name")
    override val localizedName: String,
    @ColumnInfo(name = "unlocalized_name")
    override val unlocalizedName: String,
    override val type: Int,
    override val amount: Int,
    @ColumnInfo(name = "meta_data")
    override val metaData: String?
) : RecipeElementView()