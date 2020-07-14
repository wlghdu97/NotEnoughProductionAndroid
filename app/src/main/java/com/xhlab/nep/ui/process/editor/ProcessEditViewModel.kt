package com.xhlab.nep.ui.process.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.InternalRecipeSelectionNavigationUseCase
import com.xhlab.nep.domain.ProcessCalculationNavigationUseCase
import com.xhlab.nep.domain.ProcessSelectionNavigationUseCase
import com.xhlab.nep.domain.RecipeSelectionNavigationUseCase
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import java.io.Serializable
import javax.inject.Inject

class ProcessEditViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    private val internalRecipeSelectionNavigationUseCase: InternalRecipeSelectionNavigationUseCase,
    private val recipeSelectionNavigationUseCase: RecipeSelectionNavigationUseCase,
    private val processSelectionNavigationUseCase: ProcessSelectionNavigationUseCase,
    private val calculationNavigationUseCase: ProcessCalculationNavigationUseCase,
    private val generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ProcessEditListener {

    val process = loadProcessUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableLiveData(true)
    val iconMode: LiveData<Boolean>
        get() = _iconMode

    private val _showDisconnectionAlert = LiveEvent<DisconnectionPayload>()
    val showDisconnectionAlert: LiveData<DisconnectionPayload>
        get() = _showDisconnectionAlert

    private val _connectRecipe = LiveEvent<ConnectionConstraint>()
    val connectRecipe: LiveData<ConnectionConstraint>
        get() = _connectRecipe

    private val _modificationResult = MediatorLiveData<Resource<Unit>>()
    val modificationResult: LiveData<Resource<Unit>>
        get() = _modificationResult

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
            _showDisconnectionAlert.postValue(DisconnectionPayload(from, to, element, reversed))
        } else {
            disconnect(DisconnectionPayload(from, to, element, reversed))
        }
    }

    override fun onConnectToParent(recipe: Recipe, element: ElementView, degree: Int) {
        _connectRecipe.postValue(ConnectionConstraint(requireProcessId(), true, recipe, element, degree))
    }

    override fun onConnectToChild(recipe: Recipe, element: ElementView, degree: Int) {
        _connectRecipe.postValue(ConnectionConstraint(requireProcessId(), false, recipe, element, degree))
    }

    override fun onMarkNotConsumed(recipe: Recipe, element: Element, consumed: Boolean) {
        launchSuspendFunction(_modificationResult) {
            processRepo.markNotConsumed(requireProcessId(), recipe, element, consumed)
        }
    }

    private fun disconnect(payload: DisconnectionPayload) {
        launchSuspendFunction(_modificationResult) {
            with (payload) {
                processRepo.disconnectRecipe(requireProcessId(), from, to, element, reversed)
            }
        }
    }

    fun disconnect(payload: DisconnectionPayload, disableAlert: Boolean) {
        if (disableAlert) {
            generalPreference.setShowDisconnectionAlert(false)
        }
        disconnect(payload)
    }

    fun attachSupplier(recipe: Recipe, keyElement: String) {
        launchSuspendFunction(_modificationResult) {
            val element = recipe.getInputs().find { it.unlocalizedName == keyElement }
            if (element is ElementView) {
                val supplier = SupplierRecipe(element)
                processRepo.connectRecipe(requireProcessId(), supplier, recipe, element, false)
            }
        }
    }

    fun toggleIconMode() {
        _iconMode.postValue(_iconMode.value != true)
    }

    fun navigateToInternalRecipeSelection(constraint: ConnectionConstraint) {
        invokeUseCase(
            useCase = internalRecipeSelectionNavigationUseCase,
            params = constraint
        )
    }

    fun navigateToRecipeSelection(constraint: ConnectionConstraint) {
        invokeUseCase(
            useCase = recipeSelectionNavigationUseCase,
            params = constraint
        )
    }

    fun navigateToProcessSelection(constraint: ConnectionConstraint) {
        invokeUseCase(
            useCase = processSelectionNavigationUseCase,
            params = constraint
        )
    }

    fun navigateToCalculation() {
        invokeUseCase(
            useCase = calculationNavigationUseCase,
            params = ProcessCalculationNavigationUseCase.Parameter(requireProcessId())
        )
    }

    data class ConnectionConstraint(
        val processId: String,
        val connectToParent: Boolean,
        val recipe: Recipe,
        val element: ElementView,
        val degree: Int
    ) : Serializable

    data class DisconnectionPayload(
        val from: Recipe,
        val to: Recipe,
        val element: Element,
        val reversed: Boolean
    )
}