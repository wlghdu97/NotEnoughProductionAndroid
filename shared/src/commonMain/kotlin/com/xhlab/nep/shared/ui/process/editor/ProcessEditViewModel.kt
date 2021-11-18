package com.xhlab.nep.shared.ui.process.editor

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.MR
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@ProvideWithDagger("ProcessViewModel")
class ProcessEditViewModel constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    private val generalPreference: GeneralPreference,
    private val stringResolver: StringResolver
) : ViewModel(), ProcessEditListener {

    private val _process = loadProcessUseCase.observe()
    val process = _process.transform {
        if (it.isSuccessful()) {
            emit(it.data!!)
        }
    }

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableStateFlow(true)
    val iconMode: Flow<Boolean>
        get() = _iconMode

    private val _showDisconnectionAlert = EventFlow<DisconnectionPayload>()
    val showDisconnectionAlert: Flow<DisconnectionPayload>
        get() = _showDisconnectionAlert.flow

    private val _connectRecipe = EventFlow<ConnectionConstraint>()
    val connectRecipe: Flow<ConnectionConstraint>
        get() = _connectRecipe.flow

    private val _modificationErrorMessage = EventFlow<String>()
    val modificationErrorMessage: Flow<String>
        get() = _modificationErrorMessage.flow

    private val _navigateToInternalRecipeSelection = EventFlow<ConnectionConstraint>()
    val navigateToInternalRecipeSelection: Flow<ConnectionConstraint>
        get() = _navigateToInternalRecipeSelection.flow

    private val _navigateToRecipeSelection = EventFlow<ConnectionConstraint>()
    val navigateToRecipeSelection: Flow<ConnectionConstraint>
        get() = _navigateToRecipeSelection.flow

    private val _navigateToProcessSelection = EventFlow<ConnectionConstraint>()
    val navigateToProcessSelection: Flow<ConnectionConstraint>
        get() = _navigateToProcessSelection.flow

    // processId
    private val _navigateToCalculation = EventFlow<String>()
    val navigateToCalculation: Flow<String>
        get() = _navigateToCalculation.flow

    private val modificationExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e("Failed to modify process", throwable)
        scope.launch {
            _modificationErrorMessage.emit(stringResolver.getString(MR.strings.error_failed_to_modify_process))
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

    private fun requireProcessId() =
        _process.value.data?.id ?: throw NullPointerException("process id is null.")

    override fun onDisconnect(from: Recipe, to: Recipe, element: RecipeElement, reversed: Boolean) {
        if (generalPreference.getShowDisconnectionAlert()) {
            scope.launch {
                _showDisconnectionAlert.emit(DisconnectionPayload(from, to, element, reversed))
            }
        } else {
            disconnect(DisconnectionPayload(from, to, element, reversed))
        }
    }

    override fun onConnectToParent(recipe: Recipe, element: RecipeElement, degree: Int) {
        scope.launch {
            _connectRecipe.emit(
                ConnectionConstraint(
                    processId = requireProcessId(),
                    connectToParent = true,
                    recipe = recipe,
                    element = element,
                    degree = degree
                )
            )
        }
    }

    override fun onConnectToChild(recipe: Recipe, element: RecipeElement, degree: Int) {
        scope.launch {
            _connectRecipe.emit(
                ConnectionConstraint(
                    processId = requireProcessId(),
                    connectToParent = false,
                    recipe = recipe,
                    element = element,
                    degree = degree
                )
            )
        }
    }

    override fun onMarkNotConsumed(recipe: Recipe, element: RecipeElement, consumed: Boolean) {
        scope.launch(modificationExceptionHandler) {
            processRepo.markNotConsumed(requireProcessId(), recipe, element, consumed)
        }
    }

    private fun disconnect(payload: DisconnectionPayload) {
        scope.launch(modificationExceptionHandler) {
            with(payload) {
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
        scope.launch(modificationExceptionHandler) {
            val element = recipe.getInputs().find { it.unlocalizedName == keyElement }
            if (element != null) {
                val supplier = SupplierRecipe(element)
                processRepo.connectRecipe(requireProcessId(), supplier, recipe, element, false)
            }
        }
    }

    fun toggleIconMode() {
        _iconMode.value = _iconMode.value != true
    }

    fun navigateToInternalRecipeSelection(constraint: ConnectionConstraint) {
        scope.launch {
            _navigateToInternalRecipeSelection.emit(constraint)
        }
    }

    fun navigateToRecipeSelection(constraint: ConnectionConstraint) {
        scope.launch {
            _navigateToRecipeSelection.emit(constraint)
        }
    }

    fun navigateToProcessSelection(constraint: ConnectionConstraint) {
        scope.launch {
            _navigateToProcessSelection.emit(constraint)
        }
    }

    fun navigateToCalculation() {
        scope.launch {
            _navigateToCalculation.emit(requireProcessId())
        }
    }

    @Serializable
    data class ConnectionConstraint(
        val processId: String,
        val connectToParent: Boolean,
        val recipe: Recipe,
        val element: RecipeElement,
        val degree: Int
    )

    data class DisconnectionPayload(
        val from: Recipe,
        val to: Recipe,
        val element: RecipeElement,
        val reversed: Boolean
    )
}
