package com.xhlab.nep.ui.element.recipes

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.MachineRecipeListNavigationUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.ui.main.machines.MachineListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase,
    private val machineRecipeListNavigationUseCase: MachineRecipeListNavigationUseCase
) : ViewModel(), MachineListener {

    private val elementId = MutableStateFlow<Long?>(null)

    val recipeList = loadRecipeMachineListUseCase.observeOnlySuccess()

    private val _navigateToRecipeList = EventFlow<MachineRecipeListNavigationUseCase.Parameters>()
    val navigateToRecipeList: Flow<MachineRecipeListNavigationUseCase.Parameters>
        get() = _navigateToRecipeList.flow

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        // ignore if recipe list is already loaded
        if (this.elementId.value != null) {
            return
        }
        this.elementId.value = elementId
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadRecipeMachineListUseCase,
                params = LoadRecipeMachineListUseCase.Parameter(elementId)
            )
        }
    }

    private fun requireElementId() =
        elementId.value ?: throw NullPointerException("element id is null")

    override fun onClick(machineId: Int) {
        scope.launch {
            _navigateToRecipeList.emit(
                MachineRecipeListNavigationUseCase.Parameters(requireElementId(), machineId)
            )
        }
    }

    fun navigateToRecipeList(params: MachineRecipeListNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = machineRecipeListNavigationUseCase,
            params = params
        )
    }
}
