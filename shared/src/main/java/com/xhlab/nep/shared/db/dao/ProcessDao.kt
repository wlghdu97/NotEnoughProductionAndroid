package com.xhlab.nep.shared.db.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ProcessEntity
import com.xhlab.nep.shared.db.view.RoomProcessSummary

@Dao
abstract class ProcessDao : BaseDao<ProcessEntity>() {

    @Query("""
        SELECT * FROM process
        WHERE process.process_id = :processId
    """)
    abstract suspend fun getProcess(processId: String): ProcessEntity?

    @Query("""
        SELECT process.json FROM process
        WHERE process.process_id = :processId
    """)
    abstract suspend fun getProcessJson(processId: String): String?

    @Query("""
        SELECT * FROM process
        WHERE process.process_id = :processId
    """)
    abstract fun getProcessLiveData(processId: String): LiveData<ProcessEntity?>

    @Query("""
        SELECT * FROM process_summary
        ORDER BY process_summary.name
    """)
    abstract fun getProcessList(): DataSource.Factory<Int, RoomProcessSummary>

    @Query("""
        DELETE FROM process
        WHERE process.process_id = :processId
    """)
    abstract suspend fun deleteProcess(processId: String)
}