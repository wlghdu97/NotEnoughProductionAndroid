package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.paging.PagingConfig
import kr.sparkweb.multiplatform.paging.PagingData
import kr.sparkweb.multiplatform.paging.map
import androidx.paging.Pager as AndroidXPager

actual fun <RowType : Any> createOffsetLimitPager(
    clientScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher,
    config: PagingConfig,
    queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    countQuery: Query<Long>,
    transactor: Transacter
): Pager<Int, RowType> {
    val factory = QueryDataSource.Factory {
        QueryDataSource(clientScope, ioDispatcher, queryProvider, countQuery, transactor)
    }
    return PositionalPager(ioDispatcher, config, factory)
}

private class PositionalPager<RowType : Any> : Pager<Int, RowType> {

    override val pagingData: Flow<PagingData<RowType>>

    constructor(pagingData: Flow<PagingData<RowType>>) {
        this.pagingData = pagingData
    }

    constructor(
        ioDispatcher: CoroutineDispatcher,
        config: PagingConfig,
        factory: QueryDataSource.Factory<RowType>
    ) {
        pagingData = AndroidXPager(
            config = config,
            pagingSourceFactory = factory.asPagingSourceFactory(ioDispatcher)
        ).flow
    }

    override suspend fun <R : Any> map(transform: suspend (RowType) -> R): Pager<Int, R> {
        return PositionalPager(pagingData.map { flow -> flow.map(transform) })
    }
}

