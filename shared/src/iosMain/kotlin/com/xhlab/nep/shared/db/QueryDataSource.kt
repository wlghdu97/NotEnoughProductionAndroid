package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.multiplatform.paging.PagingData
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal open class QueryDataPager<RowType : Any, V : Any> constructor(
    private val clientScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher,
    private val config: PagingConfig,
    private val queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    private val countQuery: Query<Long>,
    private val transactor: Transacter,
    private val transform: suspend (RowType) -> V
) : Pager<Int, V>(), Query.Listener {

    private val _pagingData = MutableStateFlow<PagingData<RowType>>(PagingData(emptyList()))
    override val pagingData: Flow<PagingData<V>> = _pagingData.map { data ->
        PagingData(data.map { transform(it) })
    }

    private val isInvalid = atomic(false)
    private val totalCount = MutableStateFlow(0)

    private val queryContainer = QueryContainer<RowType>()

    init {
        loadInitial(config.initialLoadSize)
    }

    override fun refresh() {
        with(queryContainer) {
            query?.removeListener(this@QueryDataPager)
            query = null
        }

        val previousSize = _pagingData.value.size
        // Do not empty previous list to prevent flickering of some views
        isInvalid.value = true
        loadInitial(previousSize)
    }

    override fun queryResultsChanged() {
        refresh()
    }

    final override fun loadInitial(initialLoadSize: Int) {
        clientScope.launch {
            val items = withContext(ioDispatcher) {
                getBlock(0, initialLoadSize)
            }
            totalCount.value = withContext(ioDispatcher) {
                getCount()
            }
            // This replaces previous invalid items
            _pagingData.value = PagingData(items)
        }
    }

    final override fun loadNext(pageSize: Int) {
        clientScope.launch {
            _pagingData.value = PagingData(
                _pagingData.value + withContext(ioDispatcher) {
                    getBlock(_pagingData.value.size, pageSize)
                }
            )
            totalCount.value = withContext(ioDispatcher) {
                getCount()
            }
        }
    }

    final override fun loadNext() {
        loadNext(config.pageSize)
    }

    private suspend fun getCount() = withContext(ioDispatcher) {
        countQuery.executeAsOne().toInt()
    }

    private suspend fun getBlock(startPosition: Int, loadSize: Int) = withContext(ioDispatcher) {
        queryContainer.query?.removeListener(this@QueryDataPager)
        val result = ArrayList<RowType>()
        queryProvider(loadSize.toLong(), startPosition.toLong()).let { query ->
            query.addListener(this@QueryDataPager)
            queryContainer.query = query
            transactor.transaction {
                result.addAll(query.executeAsList())
            }
        }
        result
    }

    override suspend fun <R : Any> map(transform: suspend (V) -> R): Pager<Int, R> {
        return QueryDataPager(
            clientScope = clientScope,
            ioDispatcher = ioDispatcher,
            config = config,
            queryProvider = queryProvider,
            countQuery = countQuery,
            transactor = transactor,
            transform = { transform(this@QueryDataPager.transform.invoke(it)) }
        )
    }
}
