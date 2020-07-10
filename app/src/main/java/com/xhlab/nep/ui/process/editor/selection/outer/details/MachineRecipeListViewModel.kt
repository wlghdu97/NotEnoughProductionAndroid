package com.xhlab.nep.ui.process.editor.selection.outer.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val loadUsageRecipeListUseCase: LoadUsageRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val elementId = MutableLiveData<Long>()
    private val machineId = MutableLiveData<Int>()
    private val connectToParent = MutableLiveData<Boolean>()

    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)
    val usageList = loadUsageRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

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

    private fun requireElementId()
            = elementId.value ?: throw NullPointerException("element id is null.")

    private fun requireMachineId()
            = machineId.value ?: throw NullPointerException("machine id is null.")

    fun searchIngredients(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
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