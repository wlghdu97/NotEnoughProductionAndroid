package com.xhlab.nep.ui.process.editor

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.MR
import com.xhlab.nep.domain.InternalRecipeSelectionNavigationUseCase
import com.xhlab.nep.domain.ProcessCalculationNavigationUseCase
import com.xhlab.nep.domain.ProcessSelectionNavigationUseCase
import com.xhlab.nep.domain.RecipeSelectionNavigationUseCase
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

class ProcessEditViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    private val internalRecipeSelectionNavigationUseCase: InternalRecipeSelectionNavigationUseCase,
    private val recipeSelectionNavigationUseCase: RecipeSelectionNavigationUseCase,
    private val processSelectionNavigationUseCase: ProcessSelectionNavigationUseCase,
    private val calculationNavigationUseCase: ProcessCalculationNavigationUseCase,
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
        val element: RecipeElement,
        val degree: Int
    ) : Serializable

    data class DisconnectionPayload(
        val from: Recipe,
        val to: Recipe,
        val element: RecipeElement,
        val reversed: Boolean
    )
}
