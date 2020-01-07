package com.xhlab.nep.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.shared.util.Resource

class BasicViewModel : BaseViewModel {
    override val refreshStatus = MutableLiveData<Resource.Status>()
    override val actionResults = MutableLiveData<Resource<Unit>>()
}

interface BaseViewModel {

    val refreshStatus: MutableLiveData<Resource.Status>
    val actionResults: MutableLiveData<Resource<Unit>>

    fun <Params, Result> invokeMediatorUseCase(
        resultData: MutableLiveData<Resource<Result>>? = null,
        useCase: MediatorUseCase<Params, Result>,
        params: Params
    ) {
        refreshStatus.value = Resource.Status.LOADING

        useCase.execute(params)
        useCase.observe().observeForever { data ->
            observeResult(data, resultData)
        }
    }

    fun <Params, Result> invokeUseCase(
        resultData: MutableLiveData<Resource<Result>>? = null,
        useCase: UseCase<Params, Result>,
        params: Params
    ): LiveData<Resource<Result>> {
        refreshStatus.value = Resource.Status.LOADING
        resultData?.value = Resource.loading(null)

        val result = useCase.invoke(params)
        result.observeForever { data ->
            observeResult(data, resultData)
        }
        return result
    }

    private fun <Result> observeResult(
        data: Resource<Result>,
        resultData: MutableLiveData<Resource<Result>>? = null
    ) {
        refreshStatus.value = data.status
        if (resultData != null) {
            resultData.value = data
        } else {
            actionResults.value = when (data.status) {
                Resource.Status.LOADING -> Resource.loading(Unit)
                Resource.Status.SUCCESS -> Resource.success(Unit)
                Resource.Status.ERROR -> Resource.error(data.exception)
            }
        }
    }
}