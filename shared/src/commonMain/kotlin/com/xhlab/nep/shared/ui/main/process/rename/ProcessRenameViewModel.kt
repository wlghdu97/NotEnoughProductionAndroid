package com.xhlab.nep.shared.ui.main.process.rename

import co.touchlab.kermit.Logger
import com.xhlab.nep.MR
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ProcessViewModel")
class ProcessRenameViewModel constructor(
    private val processRepo: ProcessRepo,
    private val stringResolver: StringResolver
) : ViewModel() {

    private val processId = MutableStateFlow<String?>(null)

    private val _name = MutableStateFlow<String?>(null)
    val name = _name.mapNotNull { it }

    private val _isNameValid = MutableStateFlow<Boolean?>(null)
    val isNameValid = _isNameValid.mapNotNull { it }

    private val _renameErrorMessage = EventFlow<String>()
    val renameErrorMessage: Flow<String>
        get() = _renameErrorMessage.flow

    private val _dismiss = EventFlow<Unit>()
    val dismiss: Flow<Unit>
        get() = _dismiss.flow

    init {
        scope.launch {
            name.collect {
                _isNameValid.value = it.isNotEmpty()
            }
        }
    }

    fun init(processId: String?, name: String?) {
        val currentProcessId = this.processId.value
        val currentName = this._name.value
        if (currentProcessId != processId && currentName != name) {
            this.processId.value = processId
            this._name.value = name
        }
    }

    private fun requireProcessId() =
        processId.value ?: throw NullPointerException("process id is null.")

    fun changeName(name: String) {
        _name.value = name
    }

    fun renameProcess() {
        when (_isNameValid.value) {
            true -> {
                val handler = CoroutineExceptionHandler { _, throwable ->
                    Logger.e("Failed to rename process", throwable)
                    emitRenameErrorMessage()
                }
                scope.launch(handler) {
                    val name = _name.value.toString()
                    processRepo.renameProcess(requireProcessId(), name)
                    _dismiss.emit(Unit)
                }
            }
            null ->
                _isNameValid.value = false
            else ->
                emitRenameErrorMessage()
        }
    }

    private fun emitRenameErrorMessage() {
        scope.launch {
            _renameErrorMessage.emit(stringResolver.getString(MR.strings.error_failed_to_rename_process))
        }
    }
}
