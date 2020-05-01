package com.xhlab.nep.shared.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.xhlab.nep.model.Machine

@Entity(
    tableName = "machine",
    indices = [Index(value = ["id", "name"], unique = true)]
)
data class MachineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mod_name")
    val modName: String,
    @ColumnInfo(name = "name", index = true)
    val name: String
) {
    fun toMachine() = Machine(
        id = id,
        modName = modName,
        name = name
    )
}