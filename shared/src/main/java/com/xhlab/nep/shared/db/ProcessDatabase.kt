package com.xhlab.nep.shared.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xhlab.nep.shared.db.dao.ProcessDao
import com.xhlab.nep.shared.db.entity.ProcessEntity

@Database(
    entities = [
        ProcessEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ProcessDatabase : RoomDatabase() {
    abstract fun getProcessDao(): ProcessDao
}