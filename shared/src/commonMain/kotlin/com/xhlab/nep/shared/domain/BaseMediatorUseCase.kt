package com.xhlab.nep.shared.domain

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import kr.sparkweb.multiplatform.domain.MediatorUseCase
import kr.sparkweb.multiplatform.util.Resource

abstract class BaseMediatorUseCase<in Params, Result> : MediatorUseCase<Params, Result>() {

    override fun onException(exception: Throwable) {
        Logger.e("MediatorUseCase crashed.", exception)
    }
}

fun <Params, Result> MediatorUseCase<Params, Result>.observeOnly(status: Resource.Status): Flow<Result?> {
    return observe().transform {
        if (it.status == status) {
            emit(it.data)
        }
    }
}

fun <Params, Result> MediatorUseCase<Params, Result>.observeOnlySuccess(): Flow<Result> {
    return observeOnly(Resource.Status.SUCCESS).mapNotNull { it }
}
