package com.xhlab.nep.ui.process.editor.selection.subprocess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.process.ProcessListener
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import javax.inject.Inject

class ProcessSelectionViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessListUseCase: LoadProcessListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ProcessListener {

    private val _constraint = MutableLiveData<ProcessEditViewModel.ConnectionConstraint>()
    val constraint: LiveData<ProcessEditViewModel.ConnectionConstraint>
        get() = _constraint

    val processList = loadProcessListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _connectionResult = LiveEvent<Resource<Unit>>()
    val connectionResult: LiveData<Resource<Unit>>
        get() = _connectionResult

    fun init(constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(constraint)
        _constraint.value = constraint
        invokeMediatorUseCase(
            useCase = loadProcessListUseCase,
            params = LoadProcessListUseCase.Parameter(constraint.element.unlocalizedName)
        )
    }

    private fun requireConstraint()
            = constraint.value ?: throw NullPointerException("constraint is null.")

    override fun onClick(id: String, name: String) {
        launchSuspendFunction(_connectionResult) {
            val constraint = requireConstraint()
            processRepo.connectProcess(
                processId = constraint.processId,
                fromProcessId = id,
                to = constraint.recipe,
                element = constraint.element
            )
        }
    }

    override fun onDelete(id: String, name: String) = Unit
    override fun onExportString(id: String) = Unit
    override fun onRename(id: String, prevName: String) = Unit
}