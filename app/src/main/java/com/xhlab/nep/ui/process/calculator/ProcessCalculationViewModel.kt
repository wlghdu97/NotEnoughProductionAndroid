package com.xhlab.nep.ui.process.calculator

import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.domain.process.ResourceRateCalculationUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessCalculationViewModel @Inject constructor(
    private val loadProcessUseCase: LoadProcessUseCase,
    private val calculationUseCase: ResourceRateCalculationUseCase,
    generalPreference: GeneralPreference
) : ViewModel() {

    private val processId = MutableStateFlow<String?>(null)

    val process = loadProcessUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _isResultValid = MutableStateFlow<Boolean?>(null)
    val isResultValid: Flow<Boolean> = _isResultValid.mapNotNull { it }

    private val _calculationResult = MutableStateFlow<ResourceRateCalculationUseCase.Result?>(null)
    val calculationResult: Flow<ResourceRateCalculationUseCase.Result> =
        _calculationResult.mapNotNull { it }

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
        scope.launch {
            processId.collect {
                if (it != null) {
                    invokeMediatorUseCase(
                        useCase = loadProcessUseCase,
                        params = LoadProcessUseCase.Parameter(it)
                    )
                }
            }
        }

        scope.launch {
            process.collect {
                val params = ResourceRateCalculationUseCase.Parameter(it)
                val result = calculationUseCase.invokeInstant(params)
                _isResultValid.value = result.isSuccessful()
                _calculationResult.value = result.data
            }
        }
    }
}
