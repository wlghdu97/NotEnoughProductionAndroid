package com.xhlab.nep.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import timber.log.Timber

class BasicViewModel : BaseViewModel {
    override val refreshStatus = MediatorLiveData<Resource.Status>()
}

interface BaseViewModel {

    val refreshStatus: MediatorLiveData<Resource.Status>

    fun <Result> ViewModel.launchSuspendFunction(
        resultData: MediatorLiveData<Resource<Result>>? = null,
        function: suspend () -> Result
    ) {
        val handler = CoroutineExceptionHandler { _, t ->
            Timber.e(t)
            resultData?.postValue(Resource.error(t))
        }
        viewModelScope.launch(handler) {
            val result = function()
            resultData?.postValue(Resource.success(result))
        }
    }

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
