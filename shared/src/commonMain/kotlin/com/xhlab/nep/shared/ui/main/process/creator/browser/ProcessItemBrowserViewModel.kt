package com.xhlab.nep.shared.ui.main.process.creator.browser

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.main.items.ElementListener
import com.xhlab.nep.shared.ui.main.machines.MachineListener
import com.xhlab.nep.shared.ui.main.process.creator.browser.details.RootRecipeSelectionListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ProcessViewModel")
class ProcessItemBrowserViewModel :
    ViewModel(),
    ElementListener,
    MachineListener,
    RootRecipeSelectionListener {

    private val elementId = MutableStateFlow<Long?>(null)

    private val _navigateToMachineList = EventFlow<Long>()
    val navigateToMachineList: Flow<Long>
        get() = _navigateToMachineList.flow

    private val _navigateToRecipeDetails = EventFlow<ElementIdWithMachineId>()
    val navigateToRecipeDetails: Flow<ElementIdWithMachineId>
        get() = _navigateToRecipeDetails.flow

    private val _returnResult = EventFlow<RecipeWithElement>()
    val returnResult: Flow<RecipeWithElement>
        get() = _returnResult.flow

    private fun requireElementId() =
        elementId.value ?: throw NullPointerException("element id is null.")

    override fun onClick(elementId: Long) {
        this.elementId.value = elementId
        scope.launch {
            _navigateToMachineList.emit(elementId)
        }
    }

    override fun onClick(machineId: Int) {
        scope.launch {
            _navigateToRecipeDetails.emit(ElementIdWithMachineId(requireElementId(), machineId))
        }
    }

    override fun onSelect(targetRecipe: Recipe, keyElement: RecipeElement) {
        scope.launch {
            _returnResult.emit(RecipeWithElement(targetRecipe, keyElement))
        }
    }

    data class ElementIdWithMachineId(val elementId: Long, val machineId: Int)

    data class RecipeWithElement(val recipe: Recipe, val element: RecipeElement)
}
