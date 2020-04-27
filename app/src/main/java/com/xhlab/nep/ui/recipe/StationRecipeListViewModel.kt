package com.xhlab.nep.ui.recipe

import androidx.lifecycle.ViewModel
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class StationRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

    fun init(elementId: Long, stationId: Int) {
        // ignore it recipe list is already loaded
        if (recipeList.value != null) {
            return
        }
        invokeMediatorUseCase(
            useCase = loadRecipeListUseCase,
            params = LoadRecipeListUseCase.Parameters(elementId, stationId)
        )
    }

    override fun onClick(elementId: Long, elementType: Int) {
        invokeUseCase(
            elementDetailNavigationUseCase,
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }
}