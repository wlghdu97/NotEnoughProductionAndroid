package com.xhlab.nep.ui.main.process.creator.browser.recipes

import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase
) : ViewModel() {

    private val elementId = MutableStateFlow<Long?>(null)

    val recipeList = loadRecipeMachineListUseCase.observeOnlySuccess()

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        val currentElementId = this.elementId.value
        if (currentElementId != elementId) {
            this.elementId.value = elementId
            scope.launch {
                invokeMediatorUseCase(
                    useCase = loadRecipeMachineListUseCase,
                    params = LoadRecipeMachineListUseCase.Parameter(elementId)
                )
            }
        }
    }
}
