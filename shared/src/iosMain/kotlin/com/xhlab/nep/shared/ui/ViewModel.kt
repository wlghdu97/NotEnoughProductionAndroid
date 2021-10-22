package com.xhlab.nep.shared.ui

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingData
import com.xhlab.multiplatform.util.CommonFlow
import com.xhlab.multiplatform.util.asCommonFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

actual open class ViewModel {
    actual val scope: CoroutineScope = MainScope()

    fun <T> toCommonFlow(flow: Flow<T>): CommonFlow<T> = flow.asCommonFlow(scope)
    fun <T> toCommonFlow(flow: StateFlow<T>): CommonFlow<T> = flow.asCommonFlow(scope)

    fun toPagingData(pager: Pager<*, *>): CommonFlow<PagingData<*>> =
        pager.pagingData.asCommonFlow(scope)
}
