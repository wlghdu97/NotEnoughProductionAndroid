package com.xhlab.nep.ui.main.process.creator.browser

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.main.process.creator.browser.details.RootRecipeSelectionListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessItemBrowserViewModel @Inject constructor() :
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

    private val _returnResult = EventFlow<Pair<Recipe, Element>>()
    val returnResult: Flow<Pair<Recipe, Element>>
        get() = _returnResult.flow

    private fun requireElementId() =
        elementId.value ?: throw NullPointerException("element id is null.")

    override fun onClick(elementId: Long, elementType: Int) {
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

    override fun onSelect(targetRecipe: Recipe, keyElement: Element) {
        scope.launch {
            _returnResult.emit(targetRecipe to keyElement)
        }
    }
}
