package com.xhlab.nep.ui.process.editor.selection.outer.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    private val loadUsageRecipeListUseCase: LoadUsageRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel() {

    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)
    val usageList = loadUsageRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    fun init(elementId: Long?, machineId: Int?, connectToParent: Boolean?) {
        if (elementId == null || machineId == null || connectToParent == null) {
            throw NullPointerException("init parameters are null.")
        }
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