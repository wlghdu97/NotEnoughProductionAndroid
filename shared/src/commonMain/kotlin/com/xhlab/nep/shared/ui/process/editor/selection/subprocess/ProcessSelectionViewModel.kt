package com.xhlab.nep.shared.ui.process.editor.selection.subprocess

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.MR
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.process.ProcessListener
import com.xhlab.nep.shared.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessViewModel")
class ProcessSelectionViewModel constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessListUseCase: LoadProcessListUseCase,
    generalPreference: GeneralPreference,
    private val stringResolver: StringResolver
) : ViewModel(), ProcessListener {

    private val _constraint = MutableStateFlow<ProcessEditViewModel.ConnectionConstraint?>(null)
    val constraint = _constraint.mapNotNull { it }

    val processList = loadProcessListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

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
        scope.launch {
            _constraint.value = constraint
            invokeMediatorUseCase(
                useCase = loadProcessListUseCase,
                params = LoadProcessListUseCase.Parameter(constraint.element.unlocalizedName)
            )
        }
    }

    private fun requireConstraint() =
        _constraint.value ?: throw NullPointerException("constraint is null.")

    override fun onClick(id: String, name: String) {
        scope.launch(connectionExceptionHandler) {
            val constraint = requireConstraint()
            processRepo.connectProcess(
                processId = constraint.processId,
                fromProcessId = id,
                to = constraint.recipe,
                element = constraint.element
            )
            _finish.emit(Unit)
        }
    }

    override fun onDelete(id: String, name: String) = Unit
    override fun onExportString(id: String) = Unit
    override fun onRename(id: String, prevName: String) = Unit
}
