package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "element",
    indices = [Index(value = ["unlocalized_name"], unique = true)]
)
data class ElementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "unlocalized_name")
    val unlocalizedName: String,
    @ColumnInfo(name = "localized_name")
    val localizedName: String,
    val type: Int = ITEM
) {
    companion object {
        const val ITEM = 0
        const val FLUID = 1
    }
}