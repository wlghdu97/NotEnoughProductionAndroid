package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "process")
data class ProcessEntity(
    @PrimaryKey
    val processId: String,
    val name: String,
    @ColumnInfo(name = "json")
    val jsonString: String
)