package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "replacement",
    primaryKeys = ["name", "element_id"],
    foreignKeys = [
        ForeignKey(
            entity = ElementEntity::class,
            parentColumns = ["id"],
            childColumns = ["element_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReplacementEntity(
    @ColumnInfo(name = "name")
    val oreDictName: String,
    @ColumnInfo(name = "element_id", index = true)
    val elementId: Long
)