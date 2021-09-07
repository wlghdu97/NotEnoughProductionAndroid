package com.xhlab.nep.ui.element.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.MachineRecipeListNavigationUseCase
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.machines.MachineListener
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase,
    private val machineRecipeListNavigationUseCase: MachineRecipeListNavigationUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    MachineListener {
    private val elementId = MutableLiveData<Long>()

    val recipeList = loadRecipeMachineListUseCase.observeOnly(Resource.Status.SUCCESS)

    private val _navigateToRecipeList = LiveEvent<MachineRecipeListNavigationUseCase.Parameters>()
    val navigateToRecipeList: LiveData<MachineRecipeListNavigationUseCase.Parameters>
        get() = _navigateToRecipeList

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        // ignore if recipe list is already loaded
        if (recipeList.value != null) {
            return
        }
        this.elementId.value = elementId
        invokeMediatorUseCase(
            useCase = loadRecipeMachineListUseCase,
            params = LoadRecipeMachineListUseCase.Parameter(elementId)
        )
    }

    private fun requireElementId() =
        elementId.value ?: throw NullPointerException("element id is null")

    override fun onClick(machineId: Int) {
        _navigateToRecipeList.postValue(
            MachineRecipeListNavigationUseCase.Parameters(requireElementId(), machineId)
        )
    }

    fun navigateToRecipeList(params: MachineRecipeListNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = machineRecipeListNavigationUseCase,
            params = params
        )
    }
}
