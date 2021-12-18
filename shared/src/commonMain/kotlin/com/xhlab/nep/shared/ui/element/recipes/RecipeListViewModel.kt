package com.xhlab.nep.shared.ui.element.recipes

import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.machines.MachineListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ViewModel")
class RecipeListViewModel constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase
) : ViewModel(), MachineListener {

    private val elementId = MutableStateFlow<Long?>(null)

    val recipeList = loadRecipeMachineListUseCase.observeOnlySuccess()

    // Pair<ElementId, MachineId>
    private val _navigateToRecipeList = EventFlow<Pair<Long, Int>>()
    val navigateToRecipeList: Flow<Pair<Long, Int>>
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
        navigateToRecipeList(requireElementId(), machineId)
    }

    private fun navigateToRecipeList(elementId: Long, machineId: Int) {
        scope.launch {
            _navigateToRecipeList.emit(elementId to machineId)
        }
    }
}
