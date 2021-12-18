package com.xhlab.nep.shared.ui.main.machines.details

import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.domain.machine.LoadMachineUseCase
import com.xhlab.nep.shared.domain.machine.MachineResultSearchUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow
import kr.sparkweb.multiplatform.util.Resource
import kr.sparkweb.multiplatform.util.Resource.Companion.isSuccessful

@ProvideWithDagger("ViewModel")
class MachineResultViewModel constructor(
    private val loadMachineUseCase: LoadMachineUseCase,
    private val machineResultSearchUseCase: MachineResultSearchUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    private val _machine = MutableStateFlow<Resource<Machine?>?>(null)
    val machine = _machine.transform {
        if (it?.isSuccessful() == true) {
            emit(it.data!!)
        }
    }

    val resultList = machineResultSearchUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<Long>()
    val navigateToDetail: Flow<Long>
        get() = _navigateToDetail.flow

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    private fun requireMachineId() =
        _machine.value?.data?.id ?: throw MachineIdNullPointerException()

    fun init(machineId: Int?) {
        if (machineId == null || machineId == -1) {
            throw MachineIdNullPointerException()
        }
        invokeUseCase(
            useCase = loadMachineUseCase,
            params = LoadMachineUseCase.Parameter(machineId),
            resultData = _machine
        )
        scope.launch {
            invokeMediatorUseCase(
                useCase = machineResultSearchUseCase,
                params = MachineResultSearchUseCase.Parameters(machineId)
            )
        }
    }

    fun searchResults(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = scope.launch {
            delay(50)
            invokeMediatorUseCase(
                useCase = machineResultSearchUseCase,
                params = MachineResultSearchUseCase.Parameters(
                    machineId = requireMachineId(),
                    term = term
                )
            )
        }
    }

    override fun onClick(elementId: Long) {
        navigateToElementDetail(elementId)
    }

    private fun navigateToElementDetail(elementId: Long) {
        scope.launch {
            _navigateToDetail.emit(elementId)
        }
    }

    class MachineIdNullPointerException : Exception("machine id is null.")
}
