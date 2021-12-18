package com.xhlab.nep.shared.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.paging.PagingData
import kr.sparkweb.multiplatform.util.CommonFlow
import kr.sparkweb.multiplatform.util.asCommonFlow

actual open class ViewModel {
    actual val scope: CoroutineScope = MainScope()

    fun <T> toCommonFlow(flow: Flow<T>): CommonFlow<T> = flow.asCommonFlow(scope)
    fun <T> toCommonFlow(flow: StateFlow<T>): CommonFlow<T> = flow.asCommonFlow(scope)

    fun toPagingData(pager: Pager<*, *>): CommonFlow<PagingData<*>> =
        pager.pagingData.asCommonFlow(scope)
}
