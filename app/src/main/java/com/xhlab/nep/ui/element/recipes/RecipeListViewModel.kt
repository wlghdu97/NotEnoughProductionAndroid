package com.xhlab.nep.ui.element.recipes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.recipe.LoadRecipeStationListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadRecipeStationListUseCase: LoadRecipeStationListUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    StationListener
{
    private val elementId = MutableLiveData<Long>()

    val recipeList = loadRecipeStationListUseCase.observeOnly(Resource.Status.SUCCESS)

    // Pair<ElementId, StationId?>
    private val _navigateToStationDetail = LiveEvent<Pair<Long, Int?>>()
    val navigateToStationDetail: LiveData<Pair<Long, Int?>>
        get() = _navigateToStationDetail

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
            useCase = loadRecipeStationListUseCase,
            params = elementId
        )
    }

    private fun requireElementId()
            = elementId.value ?: throw NullPointerException("element id is null")

    override fun onClick(stationId: Int?) {
        _navigateToStationDetail.value = requireElementId() to stationId
    }
}