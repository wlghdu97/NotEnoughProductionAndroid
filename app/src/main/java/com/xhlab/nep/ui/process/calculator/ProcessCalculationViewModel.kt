package com.xhlab.nep.ui.process.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.domain.process.ResourceRateCalculationUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessCalculationViewModel @Inject constructor(
    private val loadProcessUseCase: LoadProcessUseCase,
    private val calculationUseCase: ResourceRateCalculationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val processId = MutableLiveData<String>()

    val process = loadProcessUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _calculationResult =
        MediatorLiveData<Resource<ResourceRateCalculationUseCase.Result>>()
    val calculationResult: LiveData<Resource<ResourceRateCalculationUseCase.Result>>
        get() = _calculationResult

    fun init(processId: String?) {
        if (processId == null) {
            throw NullPointerException("process id is null.")
        }
        val currentProcessId = this.processId.value
        if (currentProcessId != processId) {
            this.processId.value = processId
        }
    }

    init {
        loadProcessUseCase.observe().addSource(processId) {
            invokeMediatorUseCase(
                useCase = loadProcessUseCase,
                params = LoadProcessUseCase.Parameter(it)
            )
        }
        loadProcessUseCase.observe().addSource(process) {
            if (it != null) {
                invokeUseCase(
                    resultData = _calculationResult,
                    useCase = calculationUseCase,
                    params = ResourceRateCalculationUseCase.Parameter(it)
                )
            }
        }
    }
}
