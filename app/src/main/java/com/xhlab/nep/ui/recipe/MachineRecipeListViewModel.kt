package com.xhlab.nep.ui.recipe

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    private val elementId = MutableStateFlow<Long?>(null)
    private val machineId = MutableStateFlow<Int?>(null)

    val recipeList = loadRecipeListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: Flow<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail.flow

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun init(elementId: Long, machineId: Int) {
        // ignore it recipe list is already loaded
        if (loadRecipeListUseCase.observe().value.data != null) {
            return
        }
        this.elementId.value = elementId
        this.machineId.value = machineId
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadRecipeListUseCase,
                params = LoadRecipeListUseCase.Parameters(elementId, machineId)
            )
        }
    }

    private fun requireElementId() =
        elementId.value ?: throw NullPointerException("element id is null.")

    private fun requireMachineId() =
        machineId.value ?: throw NullPointerException("machine id is null.")

    fun searchIngredients(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = scope.launch {
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
        scope.launch {
            _navigateToDetail.emit(
                ElementDetailNavigationUseCase.Parameters(elementId, elementType)
            )
        }
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }
}
