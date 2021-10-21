package com.xhlab.nep.ui.main.process

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ProcessEditNavigationUseCase
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.ExportProcessStringUseCase
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.util.invokeMediatorUseCase
import com.xhlab.nep.ui.util.observeOnlySuccess
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProcessListViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    loadProcessListUseCase: LoadProcessListUseCase,
    private val exportProcessStringUseCase: ExportProcessStringUseCase,
    private val processEditNavigationUseCase: ProcessEditNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ProcessListener {

    val processList = loadProcessListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _renameProcess = LiveEvent<Pair<String, String>>()
    val renameProcess: LiveData<Pair<String, String>>
        get() = _renameProcess

    private val _exportProcess = LiveEvent<Resource<String>>()
    val exportProcess: LiveData<Resource<String>>
        get() = _exportProcess

    private val _deleteProcess = LiveEvent<Pair<String, String>>()
    val deleteProcess: LiveData<Pair<String, String>>
        get() = _deleteProcess

    init {
        viewModelScope.launch {
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
        _renameProcess.postValue(id to prevName)
    }

    override fun onExportString(id: String) {
        invokeUseCase(
            resultData = _exportProcess,
            useCase = exportProcessStringUseCase,
            params = ExportProcessStringUseCase.Parameter(id)
        )
    }

    override fun onDelete(id: String, name: String) {
        _deleteProcess.postValue(id to name)
    }

    fun deleteProcess(processId: String) {
        launchSuspendFunction {
            processRepo.deleteProcess(processId)
        }
    }
}
