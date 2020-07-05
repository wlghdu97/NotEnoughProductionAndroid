package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageMachineListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase,
    private val loadUsageMachineListUseCase: LoadUsageMachineListUseCase
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val elementId = MutableLiveData<Long>()

    val recipeList = loadRecipeMachineListUseCase.observeOnly(Resource.Status.SUCCESS)
    val usageList = loadUsageMachineListUseCase.observeOnly(Resource.Status.SUCCESS)

    fun init(elementId: Long?, connectToParent: Boolean?) {
        if (elementId == null || connectToParent == null) {
            throw NullPointerException("element id is null.")
        }
        val currentElementId = this.elementId.value
        if (currentElementId != elementId) {
            this.elementId.value = elementId
            when (connectToParent) {
                true -> {
                    invokeMediatorUseCase(
                        useCase = loadUsageMachineListUseCase,
                        params = LoadUsageMachineListUseCase.Parameter(elementId)
                    )
                }
                false -> {
                    invokeMediatorUseCase(
                        useCase = loadRecipeMachineListUseCase,
                        params = LoadRecipeMachineListUseCase.Parameter(elementId)
                    )
                }
            }
        }
    }
}