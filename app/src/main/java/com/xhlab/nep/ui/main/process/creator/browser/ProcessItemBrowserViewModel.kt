package com.xhlab.nep.ui.main.process.creator.browser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.main.process.creator.browser.details.RootRecipeSelectionListener
import javax.inject.Inject

class ProcessItemBrowserViewModel @Inject constructor() : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener,
    MachineListener,
    RootRecipeSelectionListener
{
    private val elementId = MutableLiveData<Long>()

    private val _navigateToMachineList = LiveEvent<Long>()
    val navigateToMachineList: LiveData<Long>
        get() = _navigateToMachineList

    private val _navigateToRecipeDetails = LiveEvent<Pair<Long, Int>>()
    val navigateToRecipeDetails: LiveData<Pair<Long, Int>>
        get() = _navigateToRecipeDetails

    private val _returnResult = LiveEvent<Pair<Recipe, Element>>()
    val returnResult: LiveData<Pair<Recipe, Element>>
        get() = _returnResult

    private fun requireElementId()
            = elementId.value ?: throw NullPointerException("element id is null.")

    override fun onClick(elementId: Long, elementType: Int) {
        this.elementId.postValue(elementId)
        _navigateToMachineList.postValue(elementId)
    }

    override fun onClick(machineId: Int) {
        _navigateToRecipeDetails.postValue(requireElementId() to machineId)
    }

    override fun onSelect(targetRecipe: Recipe, keyElement: Element) {
        _returnResult.postValue(targetRecipe to keyElement)
    }
}