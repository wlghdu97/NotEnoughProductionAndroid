package com.xhlab.nep.ui.main.machines.details

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.domain.machine.LoadMachineUseCase
import com.xhlab.nep.shared.domain.machine.MachineResultSearchUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineResultViewModel @Inject constructor(
    private val loadMachineUseCase: LoadMachineUseCase,
    private val machineResultSearchUseCase: MachineResultSearchUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
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

    private val _navigateToDetail = EventFlow<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: Flow<ElementDetailNavigationUseCase.Parameters>
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

    override fun onClick(elementId: Long, elementType: Int) {
        scope.launch {
            _navigateToDetail.emit(
                ElementDetailNavigationUseCase.Parameters(elementId, elementType)
            )
        }
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }

    class MachineIdNullPointerException : Exception("machine id is null.")
}
