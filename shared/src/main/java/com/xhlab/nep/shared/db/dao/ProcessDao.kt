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
        WHERE process.process_id IN(:processIds)
    """)
    abstract suspend fun getProcessListById(processIds: List<String>): List<ProcessEntity?>

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
        SELECT * FROM process_summary
        WHERE process_summary.unlocalized_name = :targetElementKey
        ORDER BY process_summary.name
    """)
    abstract fun getProcessListByTarget(
        targetElementKey: String
    ): DataSource.Factory<Int, RoomProcessSummary>

    @Query("""
        DELETE FROM process
        WHERE process.process_id = :processId
    """)
    abstract suspend fun deleteProcess(processId: String)
}