package com.xhlab.nep.ui.util

import com.xhlab.multiplatform.domain.MediatorUseCase
import com.xhlab.multiplatform.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform

/**
 * TODO: temp util function
 */
suspend fun <Params, Result> invokeMediatorUseCase(
    useCase: MediatorUseCase<Params, Result>,
    params: Params
) {
    useCase.execute(Dispatchers.Default, params)
}

/**
 * TODO: temp util function
 */
fun <Params, Result> MediatorUseCase<Params, Result>.observeOnly(status: Resource.Status): Flow<Result?> {
    return observe().transform {
        if (it.status == status) {
            emit(it.data)
        }
    }
}

/**
 * TODO: temp util function
 */
fun <Params, Result> MediatorUseCase<Params, Result>.observeOnlySuccess(): Flow<Result> {
    return observeOnly(Resource.Status.SUCCESS).mapNotNull { it }
}
