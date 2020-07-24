package com.xhlab.nep.ui.main.process.rename

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessRenameViewModel @Inject constructor(
    private val processRepo: ProcessRepo
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val processId = MutableLiveData<String>()

    private val _name = MutableLiveData<String?>()
    val name: LiveData<String?>
        get() = _name

    private val _isNameValid = MediatorLiveData<Boolean?>()
    val isNameValid: LiveData<Boolean?>
        get() = _isNameValid

    private val _renameResult = LiveEvent<Resource<Unit>>()
    val renameResult: LiveData<Resource<Unit>>
        get() = _renameResult

    init {
        _isNameValid.addSource(name) {
            _isNameValid.postValue(it?.isNotEmpty())
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

    private fun requireProcessId()
            = processId.value ?: throw NullPointerException("process id is null.")

    fun changeName(name: String) {
        this._name.postValue(name)
    }

    fun renameProcess() {
        when (isNameValid.value) {
            true -> {
                val name = name.value.toString()
                launchSuspendFunction(_renameResult) {
                    processRepo.renameProcess(requireProcessId(), name)
                }
            }
            null ->
                _isNameValid.postValue(false)
            else ->
                _renameResult.postValue(Resource.error(RuntimeException()))
        }
    }
}