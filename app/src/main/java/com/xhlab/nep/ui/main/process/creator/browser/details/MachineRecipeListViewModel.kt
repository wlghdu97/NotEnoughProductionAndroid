package com.xhlab.nep.ui.main.process.creator.browser.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.recipe.LoadRecipeListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class MachineRecipeListViewModel @Inject constructor(
    private val loadRecipeListUseCase: LoadRecipeListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel() {

    val recipeList = loadRecipeListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    fun init(elementId: Long?, machineId: Int?) {
        if (elementId == null || machineId == null) {
            throw NullPointerException("init parameters are null.")
        }
        invokeMediatorUseCase(
            useCase = loadRecipeListUseCase,
            params = LoadRecipeListUseCase.Parameters(elementId, machineId)
        )
    }
}