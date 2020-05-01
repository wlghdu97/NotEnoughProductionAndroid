package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.MachineEntity

@Dao
abstract class MachineDao : BaseDao<MachineEntity>() {

    @Query("""
        SELECT machine.id FROM machine
        WHERE machine.name = :machineName
    """)
    abstract suspend fun getId(machineName: String): Int?

    @Query("""
        DELETE FROM machine
    """)
    abstract suspend fun deleteAll()

    @Transaction
    @Query("""
        SELECT * FROM machine
        ORDER BY machine.name ASC
    """)
    abstract fun getMachines(): DataSource.Factory<Int, MachineEntity>
}