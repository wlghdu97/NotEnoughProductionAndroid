package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = ElementEntity::class)
@Entity(tableName = "element_fts")
data class ElementFts(
    @ColumnInfo(name = "localized_name")
    val localizedName: String
)