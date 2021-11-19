package com.xhlab.nep.shared.ui.process.editor.selection.outer.details

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessEditorViewModel")
class MachineRecipeListViewModel constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val loadUsageRecipeListUseCase: LoadUsageRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel() {

    private val elementId = MutableStateFlow<Long?>(null)
    private val machineId = MutableStateFlow<Int?>(null)
    private val connectToParent = MutableStateFlow<Boolean?>(null)

    val recipeList = loadRecipeListUseCase.observeOnlySuccess()
    val usageList = loadUsageRecipeListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun init(elementId: Long?, machineId: Int?, connectToParent: Boolean?) {
        if (elementId == null || machineId == null || connectToParent == null) {
            throw NullPointerException("init parameters are null.")
        }
        this.elementId.value = elementId
        this.machineId.value = machineId
        this.connectToParent.value = connectToParent
        scope.launch {
            when (connectToParent) {
                true -> {
                    invokeMediatorUseCase(
                        useCase = loadUsageRecipeListUseCase,
                        params = LoadUsageRecipeListUseCase.Parameters(elementId, machineId)
                    )
                }
                false -> {
                    invokeMediatorUseCase(
                        useCase = loadRecipeListUseCase,
                        params = LoadRecipeListUseCase.Parameters(elementId, machineId)
                    )
                }
            }
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
            if (connectToParent.value == true) {
                invokeMediatorUseCase(
                    useCase = loadUsageRecipeListUseCase,
                    params = LoadUsageRecipeListUseCase.Parameters(
                        elementId = requireElementId(),
                        machineId = requireMachineId(),
                        term = term
                    )
                )
            } else {
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
}
