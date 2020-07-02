package com.xhlab.nep.ui.process.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ProcessCalculationNavigationUseCase
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessEditViewModel @Inject constructor(
    private val loadProcessUseCase: LoadProcessUseCase,
    private val calculationNavigationUseCase: ProcessCalculationNavigationUseCase,
    private val generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ProcessEditListener {

    private val _process = MediatorLiveData<Process>()
    val process: LiveData<Process>
        get() = _process

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableLiveData<Boolean>(true)
    val iconMode: LiveData<Boolean>
        get() = _iconMode

    private val _showDisconnectionAlert = LiveEvent<Unit>()
    val showDisconnectionAlert: LiveData<Unit>
        get() = _showDisconnectionAlert

    private var disconnectionPayload: DisconnectionPayload? = null

    init {
        _process.addSource(loadProcessUseCase.observe()) {
            _process.postValue(it.data)
        }
    }

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

    override fun onDisconnect(from: Recipe, to: Recipe, element: Element, reversed: Boolean) {
        if (generalPreference.getShowDisconnectionAlert()) {
            disconnectionPayload = DisconnectionPayload(from, to, element, reversed)
            _showDisconnectionAlert.postValue(Unit)
        } else {
            disconnect(DisconnectionPayload(from, to, element, reversed))
        }
    }

    override fun onMarkNotConsumed(recipe: Recipe, element: Element, consumed: Boolean) {
        val currentProcess = process.value
        if (currentProcess != null) {
            val result = currentProcess.markNotConsumed(recipe, element, consumed)
            if (result) {
                _process.postValue(currentProcess)
            }
        }
    }

    fun disconnect(disableAlert: Boolean) {
        if (disableAlert) {
            generalPreference.setShowDisconnectionAlert(false)
        }
        disconnect(disconnectionPayload)
    }

    private fun disconnect(payload: DisconnectionPayload?) {
        val currentProcess = process.value
        if (currentProcess != null && payload != null) {
            with (payload) {
                val result = currentProcess.disconnectRecipe(from, to, element, reversed)
                if (result) {
                    _process.postValue(currentProcess)
                }
            }
        }
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

    private data class DisconnectionPayload(
        val from: Recipe,
        val to: Recipe,
        val element: Element,
        val reversed: Boolean
    )
}