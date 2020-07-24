package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "ore_dict_chain",
    primaryKeys = ["chain_element_id", "element_id"],
    foreignKeys = [
        ForeignKey(
            entity = ElementEntity::class,
            parentColumns = ["id"],
            childColumns = ["element_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class OreDictChainEntity(
    @ColumnInfo(name = "chain_element_id")
    val chainId: Long,
    @ColumnInfo(name = "element_id", index = true)
    val elementId: Long
)