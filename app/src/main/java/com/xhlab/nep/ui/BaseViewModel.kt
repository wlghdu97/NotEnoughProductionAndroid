package com.xhlab.nep.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.shared.util.Resource

class BasicViewModel : BaseViewModel {
    override val refreshStatus = MediatorLiveData<Resource.Status>()
}

interface BaseViewModel {

    val refreshStatus: MediatorLiveData<Resource.Status>

    fun <Params, Result> invokeMediatorUseCase(
        useCase: MediatorUseCase<Params, Result>,
        params: Params
    ) {
        refreshStatus.value = Resource.Status.LOADING
        useCase.execute(params)
        val result = useCase.observe()
        refreshStatus.removeSource(result)
        refreshStatus.addSource(result) { data ->
            refreshStatus.postValue(data.status)
        }
    }

    fun <Params, Result> invokeUseCase(
        useCase: UseCase<Params, Result>,
        params: Params,
        resultData: MediatorLiveData<Resource<Result>>? = null
    ): LiveData<Resource<Result>> {
        refreshStatus.value = Resource.Status.LOADING
        resultData?.value = Resource.loading(null)
        val result = useCase.invoke(params)
        refreshStatus.removeSource(result)
        refreshStatus.addSource(result) { data ->
            refreshStatus.postValue(data.status)
        }
        if (resultData != null) {
            resultData.removeSource(result)
            resultData.addSource(result) { data ->
                resultData.postValue(data)
            }
        }
        return result
    }
}