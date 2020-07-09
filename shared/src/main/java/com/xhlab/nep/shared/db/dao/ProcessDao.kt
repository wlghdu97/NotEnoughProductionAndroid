package com.xhlab.nep.shared.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ProcessEntity

@Dao
abstract class ProcessDao : BaseDao<ProcessEntity>() {

    @Query("""
        SELECT * FROM process
        WHERE process.processId = :processId
    """)
    abstract suspend fun getProcess(processId: String): ProcessEntity?

    @Query("""
        SELECT * FROM process
        WHERE process.processId = :processId
    """)
    abstract fun getProcessLiveData(processId: String): LiveData<ProcessEntity?>

    @Query("""
        SELECT * FROM process
        ORDER BY process.name
    """)
    abstract fun getProcessList(): DataSource.Factory<Int, ProcessEntity>
}