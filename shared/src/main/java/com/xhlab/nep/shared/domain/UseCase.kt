package com.xhlab.nep.shared.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class UseCase<in Params, Result> {

    protected abstract suspend fun execute(params: Params): Result

    operator fun invoke(params: Params, resultData: MutableLiveData<Resource<Result>>) {
        CoroutineScope(SupervisorJob()).launch {
            try {
                resultData.postValue(Resource.success(execute(params)))
            } catch (e: Exception) {
                Timber.e(e)
                resultData.postValue(Resource.error(e))
            }
        }
    }

    operator fun invoke(params: Params): LiveData<Resource<Result>> {
        val result = MutableLiveData<Resource<Result>>()
        invoke(params, result)
        return result
    }

    suspend fun invokeInstant(params: Params): Resource<Result> {
        return try {
            Resource.success(execute(params))
        } catch (e: Exception) {
            Timber.e(e)
            Resource.error(e)
        }
    }
}