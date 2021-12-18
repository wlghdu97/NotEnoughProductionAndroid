package com.xhlab.nep.shared.ui.main.process

import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.process.ExportProcessStringUseCase
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("ProcessViewModel")
class ProcessListViewModel constructor(
    private val processRepo: ProcessRepo,
    loadProcessListUseCase: LoadProcessListUseCase,
    private val exportProcessStringUseCase: ExportProcessStringUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ProcessListener {

    val processList = loadProcessListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _renameProcess = EventFlow<ProcessIdName>()
    val renameProcess: Flow<ProcessIdName>
        get() = _renameProcess.flow

    private val _showExportStringDialog = EventFlow<String>()
    val showExportStringDialog: Flow<String>
        get() = _showExportStringDialog.flow

    private val _showExportFailedMessage = EventFlow<Unit>()
    val showExportFailedMessage: Flow<Unit>
        get() = _showExportFailedMessage.flow

    private val _deleteProcess = EventFlow<ProcessIdName>()
    val deleteProcess: Flow<ProcessIdName>
        get() = _deleteProcess.flow

    // ProcessId
    private val _navigateToProcessEdit = EventFlow<String>()
    val navigateToProcessEdit: Flow<String>
        get() = _navigateToProcessEdit.flow

    init {
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadProcessListUseCase,
                params = LoadProcessListUseCase.Parameter()
            )
        }
    }

    override fun onClick(id: String, name: String) {
        scope.launch {
            _navigateToProcessEdit.emit(id)
        }
    }

    override fun onRename(id: String, prevName: String) {
        scope.launch {
            _renameProcess.emit(ProcessIdName(id, prevName))
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
            _deleteProcess.emit(ProcessIdName(id, name))
        }
    }

    fun deleteProcess(processId: String) {
        scope.launch {
            processRepo.deleteProcess(processId)
        }
    }

    data class ProcessIdName(val id: String, val name: String)
}
