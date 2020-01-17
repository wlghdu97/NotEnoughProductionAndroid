package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "replacement",
    primaryKeys = ["rep_id", "element_id"],
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
    @ColumnInfo(name = "rep_id")
    val replacementId: Long = 0,
    @ColumnInfo(name = "element_id", index = true)
    val elementId: Long
)