package com.xhlab.nep.shared.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.xhlab.nep.shared.util.Resource
import timber.log.Timber

abstract class MediatorUseCase<in Params, Result> {

    protected val result = MediatorLiveData<Resource<Result>>()

    protected abstract fun executeInternal(params: Params): LiveData<Resource<Result>>

    fun execute(params: Params) {
        try {
            val liveData = executeInternal(params)

            result.removeSource(liveData)
            result.addSource(liveData) {
                result.postValue(it)
            }
        } catch (e: Exception) {
            Timber.e(e)
            result.postValue(Resource.error(e))
        }
    }

    fun observe(): MediatorLiveData<Resource<Result>> = result

    fun observeOnly(status: Resource.Status): LiveData<Result?> = Transformations.map(result) {
        if (it.status == status) { it.data } else null
    }
}