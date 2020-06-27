package com.xhlab.nep.ui.process.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.domain.ProcessCalculationNavigationUseCase
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessEditViewModel @Inject constructor(
    private val loadProcessUseCase: LoadProcessUseCase,
    private val calculationNavigationUseCase: ProcessCalculationNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel() {

    val process = loadProcessUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableLiveData<Boolean>(true)
    val iconMode: LiveData<Boolean>
        get() = _iconMode

    fun init(processId: String?) {
        if (processId == null) {
            throw NullPointerException("process id is null.")
        }
        invokeMediatorUseCase(
            useCase = loadProcessUseCase,
            params = LoadProcessUseCase.Parameter(processId)
        )
    }

    fun toggleIconMode() {
        _iconMode.postValue(_iconMode.value != true)
    }

    fun navigateToCalculation() {
        val processId = process.value?.id
        if (processId != null) {
            invokeUseCase(
                useCase = calculationNavigationUseCase,
                params = ProcessCalculationNavigationUseCase.Parameter(processId)
            )
        }
    }
}