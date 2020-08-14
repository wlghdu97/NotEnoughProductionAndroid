package com.xhlab.nep.ui.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    private val elementId = MutableLiveData<Long>()
    private val machineId = MutableLiveData<Int>()

    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = LiveEvent<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: LiveData<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun init(elementId: Long, machineId: Int) {
        // ignore it recipe list is already loaded
        if (recipeList.value != null) {
            return
        }
        this.elementId.value = elementId
        this.machineId.value = machineId
        invokeMediatorUseCase(
            useCase = loadRecipeListUseCase,
            params = LoadRecipeListUseCase.Parameters(elementId, machineId)
        )
    }

    private fun requireElementId()
            = elementId.value ?: throw NullPointerException("element id is null.")

    private fun requireMachineId()
            = machineId.value ?: throw NullPointerException("machine id is null.")

    fun searchIngredients(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(50)
            invokeMediatorUseCase(
                useCase = loadRecipeListUseCase,
                params = LoadRecipeListUseCase.Parameters(
                    elementId = requireElementId(),
                    machineId = requireMachineId(),
                    term = term
                )
            )
        }
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToDetail.postValue(
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }
}