package com.xhlab.nep.ui.main.process.importer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.process.ImportProcessStringUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessImportViewModel @Inject constructor(
    private val importProcessStringUseCase: ImportProcessStringUseCase
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val _isStringValid = MediatorLiveData<Boolean?>()
    val isStringValid: LiveData<Boolean?>
        get() = _isStringValid

    val importResult = MediatorLiveData<Resource<Unit>>()

    init {
        _isStringValid.addSource(importResult) {
            if (it.throwable != null) {
                _isStringValid.postValue(false)
            }
        }
    }

    fun notifyTextChanged() {
        _isStringValid.postValue(true)
    }

    fun importProcess(string: String) {
        invokeUseCase(
            resultData = importResult,
            useCase = importProcessStringUseCase,
            params = ImportProcessStringUseCase.Parameter(string)
        )
    }
}
