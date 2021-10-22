package com.xhlab.nep.ui.main.process.creator.browser.details

import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel() {

    private val elementId = MutableStateFlow<Long?>(null)
    private val machineId = MutableStateFlow<Int?>(null)

    val recipeList = loadRecipeListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun init(elementId: Long?, machineId: Int?) {
        if (elementId == null || machineId == null) {
            throw NullPointerException("init parameters are null.")
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
}
