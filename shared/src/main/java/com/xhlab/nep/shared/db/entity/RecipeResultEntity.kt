package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "recipe_result",
    primaryKeys = ["recipe_id", "result_item_id"],
    foreignKeys = [
        ForeignKey(
            entity = ElementEntity::class,
            parentColumns = ["id"],
            childColumns = ["result_item_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RecipeResultEntity(
    @ColumnInfo(name = "recipe_id")
    val recipeId: Long,
    @ColumnInfo(name = "result_item_id", index = true)
    val resultItemId: Int,
    val amount: Int
)