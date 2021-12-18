package com.xhlab.nep.shared.db

import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.*

@Suppress("deprecation")
internal class QueryDataSource<RowType : Any> constructor(
    private val clientScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    private val countQuery: Query<Long>,
    private val transactor: Transacter
) : PositionalDataSource<RowType>(), Query.Listener {

    private val queryContainer = QueryContainer<RowType>()
    private val callbacks = linkedSetOf<InvalidatedCallback>()

    override fun invalidate() {
        with(queryContainer) {
            query?.removeListener(this@QueryDataSource)
            query = null
        }
        super.invalidate()
    }

    override fun addInvalidatedCallback(onInvalidatedCallback: InvalidatedCallback) {
        super.addInvalidatedCallback(onInvalidatedCallback)
        if (callbacks.isEmpty()) {
            queryContainer.query?.addListener(this)
        }
        callbacks.add(onInvalidatedCallback)
    }

    override fun removeInvalidatedCallback(onInvalidatedCallback: InvalidatedCallback) {
        super.removeInvalidatedCallback(onInvalidatedCallback)
        callbacks.remove(onInvalidatedCallback)
        if (callbacks.isEmpty()) {
            queryContainer.query?.removeListener(this)
        }
    }

    override fun queryResultsChanged() {
        invalidate()
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<RowType>) {
        clientScope.launch {
            val items = getBlock(params.requestedStartPosition, params.requestedLoadSize)
            val totalCount = getCount()
            callback.onResult(items, params.requestedStartPosition, totalCount)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<RowType>) {
        clientScope.launch {
            val items = getBlock(params.startPosition, params.loadSize)
            callback.onResult(items)
        }
    }

    private suspend fun getCount() = withContext(ioDispatcher) {
        countQuery.executeAsOne().toInt()
    }

    private suspend fun getBlock(startPosition: Int, loadSize: Int) = withContext(ioDispatcher) {
        queryContainer.query?.removeListener(this@QueryDataSource)
        val result = ArrayList<RowType>()
        queryProvider(loadSize.toLong(), startPosition.toLong()).let { query ->
            if (callbacks.isNotEmpty()) {
                query.addListener(this@QueryDataSource)
            }
            queryContainer.query = query
            if (!isInvalid) {
                transactor.transaction {
                    result.addAll(query.executeAsList())
                }
            }
        }
        result
    }

    class Factory<T : Any>(
        private val delegate: () -> QueryDataSource<T>
    ) : DataSource.Factory<Int, T>() {

        override fun create(): DataSource<Int, T> {
            return delegate()
        }
    }
}
