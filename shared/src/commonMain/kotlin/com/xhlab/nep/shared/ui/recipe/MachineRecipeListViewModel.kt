package com.xhlab.nep.shared.ui.recipe

import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ViewModel")
class MachineRecipeListViewModel constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    private val elementId = MutableStateFlow<Long?>(null)
    private val machineId = MutableStateFlow<Int?>(null)

    val recipeList = loadRecipeListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<Long>()
    val navigateToDetail: Flow<Long>
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

    override fun onClick(elementId: Long) {
        navigateToElementDetail(elementId)
    }

    private fun navigateToElementDetail(elementId: Long) {
        scope.launch {
            _navigateToDetail.emit(elementId)
        }
    }
}
