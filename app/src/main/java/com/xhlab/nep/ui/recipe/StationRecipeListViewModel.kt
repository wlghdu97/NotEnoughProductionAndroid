package com.xhlab.nep.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class StationRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

    // Pair<ElementId, ElementType>
    private val _navigateToElementDetail = LiveEvent<Pair<Long, Int>>()
    val navigateToElementDetail: LiveData<Pair<Long, Int>>
        get() = _navigateToElementDetail

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
        _navigateToElementDetail.value = elementId to elementType
    }
}