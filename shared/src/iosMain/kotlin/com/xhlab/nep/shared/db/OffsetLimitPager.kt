package com.xhlab.nep.shared.db

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

actual fun <RowType : Any> createOffsetLimitPager(
    clientScope: CoroutineScope,
    ioDispatcher: CoroutineDispatcher,
    config: PagingConfig,
    queryProvider: (limit: Long, offset: Long) -> Query<RowType>,
    countQuery: Query<Long>,
    transactor: Transacter
): Pager<Int, RowType> {
    return QueryDataPager(
        clientScope,
        ioDispatcher,
        config,
        queryProvider,
        countQuery,
        transactor
    ) { it }
}
