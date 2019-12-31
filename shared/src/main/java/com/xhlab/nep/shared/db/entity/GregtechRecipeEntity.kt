package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "gregtech_recipe",
    primaryKeys = ["recipe_id", "target_item_id"],
    foreignKeys = [
        ForeignKey(
            entity = ElementEntity::class,
            parentColumns = ["id"],
            childColumns = ["target_item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class GregtechRecipeEntity(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,
    @ColumnInfo(name = "target_item_id", index = true)
    val targetItemId: Long,
    val amount: Int,
    @ColumnInfo(name = "machine_id", index = true)
    val machineId: Int,
    @ColumnInfo(name = "enabled")
    val isEnabled: Boolean,
    val duration: Int,
    val eut: Int
)