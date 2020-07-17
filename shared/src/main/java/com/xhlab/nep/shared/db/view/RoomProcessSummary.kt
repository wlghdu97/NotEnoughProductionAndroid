package com.xhlab.nep.shared.db.view

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import com.xhlab.nep.model.process.ProcessSummary

@DatabaseView(
    viewName = "process_summary",
    value = """
        SELECT 
        process.process_id,
        process.name,
        process.unlocalized_name,
        process.localized_name,
        process.amount,
        process.node_count,
        process.sub_process_count
        FROM process
    """
)
data class RoomProcessSummary(
    @ColumnInfo(name = "process_id")
    override val processId: String,
    override val name: String,
    @ColumnInfo(name = "unlocalized_name")
    override val unlocalizedName: String,
    @ColumnInfo(name = "localized_name")
    override val localizedName: String,
    override val amount: Int,
    @ColumnInfo(name = "node_count")
    override val nodeCount: Int,
    @ColumnInfo(name = "sub_process_count")
    override val subProcessCount: Int
) : ProcessSummary()