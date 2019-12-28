package com.xhlab.nep.shared.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.GregtechMachineEntity

@Dao
abstract class GregtechMachineDao : BaseDao<GregtechMachineEntity>() {

    @Query("""
        SELECT gregtech_machine.id FROM gregtech_machine
        WHERE gregtech_machine.name = :name
    """)
    abstract suspend fun getId(name: String): Int
}