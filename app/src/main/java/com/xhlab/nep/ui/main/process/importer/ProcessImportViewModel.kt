package com.xhlab.nep.ui.main.process.importer

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.shared.domain.process.ImportProcessStringUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessImportViewModel @Inject constructor(
    private val importProcessStringUseCase: ImportProcessStringUseCase
) : ViewModel() {

    private val _isStringValid = MutableStateFlow<Boolean?>(null)
    val isStringValid = _isStringValid.mapNotNull { it }

    private val importResult = MutableStateFlow<Resource<Unit>?>(null)

    private val _dismiss = EventFlow<Unit>()
    val dismiss: Flow<Unit>
        get() = _dismiss.flow

    init {
        scope.launch {
            importResult.collect {
                when (it?.status) {
                    Resource.Status.SUCCESS -> {
                        _dismiss.emit(Unit)
                    }
                    Resource.Status.ERROR -> {
                        _isStringValid.emit(false)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun notifyTextChanged() {
        scope.launch {
            _isStringValid.emit(true)
        }
    }

    fun importProcess(string: String) {
        invokeUseCase(
            resultData = importResult,
            useCase = importProcessStringUseCase,
            params = ImportProcessStringUseCase.Parameter(string)
        )
    }
}
