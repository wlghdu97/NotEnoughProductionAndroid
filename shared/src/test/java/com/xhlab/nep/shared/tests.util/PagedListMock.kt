package com.xhlab.nep.shared.tests.util

import android.database.Cursor
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.room.RoomDatabase
import androidx.room.RoomSQLiteQuery
import androidx.room.paging.LimitOffsetDataSource
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlin.math.max

fun <T> List<T>.asPagedList(config: PagedList.Config? = null): PagedList<T>? {
    val size = max(1, size)

    val defaultConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(size)
        .setMaxSize(size + 2)
        .setPrefetchDistance(1)
        .build()

    return LiveDataTestUtil.getValue(LivePagedListBuilder<Int, T>(
        createMockDataSourceFactory(this),
        config ?: defaultConfig
    ).build())
}

fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
    object : DataSource.Factory<Int, T>() {
        override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
    }

private val mockQuery = mock<RoomSQLiteQuery> {
    on { sql }.doReturn("")
}

private val mockDB = mock<RoomDatabase> {
    on { invalidationTracker }.doReturn(mock())
}

class MockLimitDataSource<T>(
    private val itemList: List<T>
) : LimitOffsetDataSource<T>(mockDB, mockQuery, false, null) {

    override fun convertRows(cursor: Cursor?): MutableList<T> = itemList.toMutableList()
    override fun countItems(): Int = itemList.count()
    override fun isInvalid(): Boolean = false

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<T>) {
        //callback.onResult(itemList.toMutableList())
    }

    override fun loadRange(startPosition: Int, loadCount: Int): MutableList<T> {
        return itemList.subList(startPosition, startPosition + loadCount).toMutableList()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<T>) {
        callback.onResult(itemList, 0, itemList.size)
    }
}