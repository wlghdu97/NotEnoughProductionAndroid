package com.xhlab.nep.ui.process.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ProcessCalculationNavigationUseCase
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessEditViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    private val calculationNavigationUseCase: ProcessCalculationNavigationUseCase,
    private val generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ProcessEditListener {

    val process = loadProcessUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableLiveData<Boolean>(true)
    val iconMode: LiveData<Boolean>
        get() = _iconMode

    private val _showDisconnectionAlert = LiveEvent<Unit>()
    val showDisconnectionAlert: LiveData<Unit>
        get() = _showDisconnectionAlert

    private var disconnectionPayload: DisconnectionPayload? = null

    fun init(processId: String?) {
        if (processId == null) {
            throw NullPointerException("process id is null.")
        }
        invokeMediatorUseCase(
            useCase = loadProcessUseCase,
            params = LoadProcessUseCase.Parameter(processId)
        )
    }

    private fun requireProcessId()
            = process.value?.id ?: throw NullPointerException("process id is null.")

    override fun onDisconnect(from: Recipe, to: Recipe, element: Element, reversed: Boolean) {
        if (generalPreference.getShowDisconnectionAlert()) {
            disconnectionPayload = DisconnectionPayload(from, to, element, reversed)
            _showDisconnectionAlert.postValue(Unit)
        } else {
            disconnect(DisconnectionPayload(from, to, element, reversed))
        }
    }

    override fun onMarkNotConsumed(recipe: Recipe, element: Element, consumed: Boolean) {
        launchSuspendFunction {
            processRepo.markNotConsumed(requireProcessId(), recipe, element, consumed)
        }
    }

    private fun disconnect(payload: DisconnectionPayload?) {
        launchSuspendFunction {
            if (payload != null) with (payload) {
                processRepo.disconnectRecipe(requireProcessId(), from, to, element, reversed)
            }
        }
    }

    fun disconnect(disableAlert: Boolean) {
        if (disableAlert) {
            generalPreference.setShowDisconnectionAlert(false)
        }
        disconnect(disconnectionPayload)
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

    private data class DisconnectionPayload(
        val from: Recipe,
        val to: Recipe,
        val element: Element,
        val reversed: Boolean
    )
}