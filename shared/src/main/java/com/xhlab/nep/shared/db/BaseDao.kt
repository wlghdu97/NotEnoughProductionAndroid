package com.xhlab.nep.shared.db

import androidx.room.*

@Dao
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(list: List<T>): List<Long>

    @Update
    abstract suspend fun update(obj: T)

    @Update
    abstract suspend fun update(list: List<T>)

    @Delete
    abstract suspend fun delete(obj: T)

    @Transaction
    open suspend fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    @Transaction
    open suspend fun upsert(list: List<T>) {
        val insertResults = insert(list)
        val insertList = arrayListOf<T>()

        for ((index, obj) in list.withIndex()) {
            if (insertResults[index] == -1L) {
                insertList.add(obj)
            }
        }

        if (insertList.isNotEmpty()) {
            update(insertList)
        }
    }
}