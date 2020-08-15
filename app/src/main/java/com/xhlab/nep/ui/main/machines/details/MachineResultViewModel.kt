package com.xhlab.nep.ui.main.machines.details

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.domain.machine.MachineResultSearchUseCase
import com.xhlab.nep.shared.domain.machine.LoadMachineUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineResultViewModel @Inject constructor(
    private val loadMachineUseCase: LoadMachineUseCase,
    private val machineResultSearchUseCase: MachineResultSearchUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ElementListener {

    private val _machine = MediatorLiveData<Resource<Machine?>>()
    val machine = Transformations.map(_machine) {
        if (it.isSuccessful()) it.data else null
    }

    val resultList = machineResultSearchUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = LiveEvent<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: LiveData<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    private fun requireMachineId()
            = machine.value?.id ?: throw MachineIdNullPointerException()

    fun init(machineId: Int?) {
        if (machineId == null || machineId == -1) {
            throw MachineIdNullPointerException()
        }
        invokeUseCase(
            useCase = loadMachineUseCase,
            params = LoadMachineUseCase.Parameter(machineId),
            resultData = _machine
        )
        invokeMediatorUseCase(
            useCase = machineResultSearchUseCase,
            params = MachineResultSearchUseCase.Parameters(machineId)
        )
    }

    fun searchResults(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
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
        _navigateToDetail.postValue(
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }

    class MachineIdNullPointerException : Exception("machine id is null.")
}