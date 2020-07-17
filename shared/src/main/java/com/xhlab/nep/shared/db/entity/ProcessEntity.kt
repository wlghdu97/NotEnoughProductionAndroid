package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "process")
data class ProcessEntity(
    @PrimaryKey
    @ColumnInfo(name = "process_id")
    val processId: String,
    val name: String,
    @ColumnInfo(name = "unlocalized_name")
    val unlocalizedName: String,
    @ColumnInfo(name = "localized_name")
    val localizedName: String,
    val amount: Int,
    @ColumnInfo(name = "node_count")
    val nodeCount: Int,
    @ColumnInfo(name = "json")
    val jsonString: String
)