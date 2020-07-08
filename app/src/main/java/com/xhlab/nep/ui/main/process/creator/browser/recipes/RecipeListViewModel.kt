package com.xhlab.nep.ui.main.process.creator.browser.recipes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val elementId = MutableLiveData<Long>()

    val recipeList = loadRecipeMachineListUseCase.observeOnly(Resource.Status.SUCCESS)

    fun init(elementId: Long?) {
        if (elementId == null) {
            throw NullPointerException("element id is null.")
        }
        val currentElementId = this.elementId.value
        if (currentElementId != elementId) {
            this.elementId.value = elementId
            invokeMediatorUseCase(
                useCase = loadRecipeMachineListUseCase,
                params = LoadRecipeMachineListUseCase.Parameter(elementId)
            )
        }
    }
}