package com.xhlab.nep.ui.main.process

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.domain.ProcessEditNavigationUseCase
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.process.ExportProcessStringUseCase
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessListViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    loadProcessListUseCase: LoadProcessListUseCase,
    private val exportProcessStringUseCase: ExportProcessStringUseCase,
    private val processEditNavigationUseCase: ProcessEditNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ProcessListener {

    val processList = loadProcessListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _renameProcess = EventFlow<Pair<String, String>>()
    val renameProcess: Flow<Pair<String, String>>
        get() = _renameProcess.flow

    private val _showExportStringDialog = EventFlow<String>()
    val showExportStringDialog: Flow<String>
        get() = _showExportStringDialog.flow

    private val _showExportFailedMessage = EventFlow<Unit>()
    val showExportFailedMessage: Flow<Unit>
        get() = _showExportFailedMessage.flow

    private val _deleteProcess = EventFlow<Pair<String, String>>()
    val deleteProcess: Flow<Pair<String, String>>
        get() = _deleteProcess.flow

    init {
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadProcessListUseCase,
                params = LoadProcessListUseCase.Parameter()
            )
        }
    }

    override fun onClick(id: String, name: String) {
        invokeUseCase(
            useCase = processEditNavigationUseCase,
            params = ProcessEditNavigationUseCase.Parameter(id)
        )
    }

    override fun onRename(id: String, prevName: String) {
        scope.launch {
            _renameProcess.emit(id to prevName)
        }
    }

    override fun onExportString(id: String) {
        scope.launch {
            val params = ExportProcessStringUseCase.Parameter(id)
            val result = exportProcessStringUseCase.invokeInstant(params)
            if (result.status == Resource.Status.SUCCESS) {
                _showExportStringDialog.emit(result.data!!)
            } else {
                _showExportFailedMessage.emit(Unit)
            }
        }
    }

    override fun onDelete(id: String, name: String) {
        scope.launch {
            _deleteProcess.emit(id to name)
        }
    }

    fun deleteProcess(processId: String) {
        scope.launch {
            processRepo.deleteProcess(processId)
        }
    }
}
