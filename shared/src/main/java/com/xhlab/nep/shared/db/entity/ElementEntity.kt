package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "element",
    indices = [Index(value = ["unlocalized_name", "meta_data"], unique = true)]
)
data class ElementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "unlocalized_name")
    val unlocalizedName: String,
    @ColumnInfo(name = "localized_name")
    val localizedName: String,
    val type: Int = ITEM,
    @ColumnInfo(name = "meta_data")
    val metaData: String = ""
) {
    companion object {
        const val ITEM = 0
        const val FLUID = 1
        const val ORE_DICT = 2
        const val ORE_CHAIN = 3
    }
}