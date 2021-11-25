package com.xhlab.nep.shared.ui.main.process.creator.browser

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.main.items.ElementListener
import com.xhlab.nep.shared.ui.main.machines.MachineListener
import com.xhlab.nep.shared.ui.main.process.creator.browser.details.RootRecipeSelectionListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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

    private val _navigateToRecipeDetails = EventFlow<Pair<Long, Int>>()
    val navigateToRecipeDetails: Flow<Pair<Long, Int>>
        get() = _navigateToRecipeDetails.flow

    private val _returnResult = EventFlow<Pair<Recipe, RecipeElement>>()
    val returnResult: Flow<Pair<Recipe, RecipeElement>>
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
            _navigateToRecipeDetails.emit(requireElementId() to machineId)
        }
    }

    override fun onSelect(targetRecipe: Recipe, keyElement: RecipeElement) {
        scope.launch {
            _returnResult.emit(targetRecipe to keyElement)
        }
    }
}
