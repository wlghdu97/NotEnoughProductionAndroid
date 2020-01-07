package com.xhlab.nep.shared.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber

abstract class UseCase<in Params, Result> {

    protected abstract suspend fun execute(params: Params): Result

    operator fun invoke(params: Params, resultData: MutableLiveData<Resource<Result>>) {
        resultData.value = Resource.loading(null)

        val result = invokeFunction(params)
        result.observeForever { resultData.value = it }
    }

    operator fun invoke(params: Params): LiveData<Resource<Result>> {
        return invokeFunction(params)
    }

    private fun invokeFunction(params: Params) = liveData(SupervisorJob()) {
        try {
            val result = execute(params)
            emit(Resource.success(result))
        } catch (e: Exception) {
            Timber.e(e)
            emit(Resource.error(e))
        }
    }
}