package com.xhlab.nep.shared.util

import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

actual val testCoroutineContext: CoroutineContext =
    Executors.newSingleThreadExecutor().asCoroutineDispatcher()

actual fun runBlockingTest(
    context: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) = runBlocking(testCoroutineContext) { this.block() }

inline fun <reified Result> Flow<Resource<Result>>.dropLoading(): Flow<Resource<Result>> {
    return dropWhile { it.status == Resource.Status.LOADING }
}
