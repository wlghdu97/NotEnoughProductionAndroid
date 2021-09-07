package com.xhlab.nep.ui.process.editor.selection.outer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import javax.inject.Inject

class RecipeSelectionViewModel @Inject constructor(
    private val processRepo: ProcessRepo
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    RecipeSelectionListener,
    OreDictRecipeSelectionListener {
    private val _constraint = MutableLiveData<ProcessEditViewModel.ConnectionConstraint>()
    val constraint: LiveData<ProcessEditViewModel.ConnectionConstraint>
        get() = _constraint

    private val _connectionResult = LiveEvent<Resource<Unit>>()
    val connectionResult: LiveData<Resource<Unit>>
        get() = _connectionResult

    fun init(constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(constraint)
        _constraint.postValue(constraint)
    }

    private fun requireProcessId() =
        constraint.value?.processId ?: throw NullPointerException("process id is null.")

    override fun onSelect(from: Recipe, to: Recipe, element: Element, reversed: Boolean) {
        launchSuspendFunction(_connectionResult) {
            processRepo.connectRecipe(requireProcessId(), from, to, element, reversed)
        }
    }

    override fun onSelectOreDict(
        from: Recipe,
        to: Recipe,
        target: Element,
        ingredient: Element,
        reversed: Boolean
    ) {
        launchSuspendFunction(_connectionResult) {
            if (target is ElementView && ingredient is ElementView) {
                val bridgeRecipe = OreChainRecipe(target, ingredient)
                val processId = requireProcessId()
                processRepo.connectRecipe(processId, bridgeRecipe, to, target, reversed)
                processRepo.connectRecipe(processId, from, bridgeRecipe, ingredient, reversed)
            }
        }
    }
}
