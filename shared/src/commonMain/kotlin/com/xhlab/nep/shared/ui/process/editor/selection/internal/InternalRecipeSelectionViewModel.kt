package com.xhlab.nep.shared.ui.process.editor.selection.internal

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.MR
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.shared.ui.process.editor.selection.RecipeSelectionListener
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessEditorViewModel")
class InternalRecipeSelectionViewModel constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    generalPreference: GeneralPreference,
    private val stringResolver: StringResolver
) : ViewModel(), RecipeSelectionListener {

    private val _process = loadProcessUseCase.observe()
    val process = _process.transform {
        if (it.isSuccessful()) {
            emit(it.data!!)
        }
    }

    private val _constraint = MutableStateFlow<ProcessEditViewModel.ConnectionConstraint?>(null)
    val constraint = _constraint.mapNotNull { it }

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableStateFlow(true)
    val iconMode: Flow<Boolean>
        get() = _iconMode

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
        invokeMediatorUseCase(
            useCase = loadProcessUseCase,
            params = LoadProcessUseCase.Parameter(constraint.processId)
        )
        _constraint.value = constraint
    }

    private fun requireProcessId() =
        _process.value.data?.id ?: throw NullPointerException("process id is null.")

    override fun onSelect(from: Recipe, to: Recipe, element: RecipeElement, reversed: Boolean) {
        scope.launch(connectionExceptionHandler) {
            processRepo.connectRecipe(requireProcessId(), from, to, element, reversed)
            _finish.emit(Unit)
        }
    }

    fun toggleIconMode() {
        _iconMode.value = _iconMode.value != true
    }
}
