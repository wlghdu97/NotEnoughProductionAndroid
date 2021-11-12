package com.xhlab.nep.ui.process.editor.selection.outer

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.MR
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.util.StringResolver
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecipeSelectionViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val stringResolver: StringResolver
) : ViewModel(), RecipeSelectionListener, OreDictRecipeSelectionListener {

    private val _constraint = MutableStateFlow<ProcessEditViewModel.ConnectionConstraint?>(null)
    val constraint = _constraint.mapNotNull { it }

    private val _connectionErrorMessage = EventFlow<String>()
    val connectionErrorMessage: Flow<String>
        get() = _connectionErrorMessage.flow

    private val _finish = EventFlow<Unit>()
    val finish: Flow<Unit>
        get() = _finish.flow

    private val connectionExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Logger.e("Failed to connect process recipes", throwable)
        scope.launch {
            _connectionErrorMessage.emit(stringResolver.getString(MR.strings.error_connect_recipe_failed))
        }
    }

    fun init(constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(constraint)
        _constraint.value = constraint
    }

    private fun requireProcessId() =
        _constraint.value?.processId ?: throw NullPointerException("process id is null.")

    override fun onSelect(from: Recipe, to: Recipe, element: RecipeElement, reversed: Boolean) {
        scope.launch(connectionExceptionHandler) {
            processRepo.connectRecipe(requireProcessId(), from, to, element, reversed)
            _finish.emit(Unit)
        }
    }

    override fun onSelectOreDict(
        from: Recipe,
        to: Recipe,
        target: RecipeElement,
        ingredient: RecipeElement,
        reversed: Boolean
    ) {
        scope.launch(connectionExceptionHandler) {
            val bridgeRecipe = OreChainRecipe(target, ingredient)
            val processId = requireProcessId()
            processRepo.connectRecipe(processId, bridgeRecipe, to, target, reversed)
            processRepo.connectRecipe(processId, from, bridgeRecipe, ingredient, reversed)
            _finish.emit(Unit)
        }
    }
}
