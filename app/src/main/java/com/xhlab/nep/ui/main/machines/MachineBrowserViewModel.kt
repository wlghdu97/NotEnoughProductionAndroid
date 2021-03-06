package com.xhlab.nep.ui.main.machines

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.MachineResultNavigationUseCase
import com.xhlab.nep.shared.domain.machine.LoadMachineListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class MachineBrowserViewModel @Inject constructor(
    loadMachineListUseCase: LoadMachineListUseCase,
    generalPreference: GeneralPreference,
    private val machineResultNavigationUseCase: MachineResultNavigationUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    MachineListener
{
    val machineList = loadMachineListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isDBLoaded = generalPreference.isDBLoaded

    private val _navigateToMachineResult = LiveEvent<MachineResultNavigationUseCase.Parameter>()
    val navigateToMachineResult: LiveData<MachineResultNavigationUseCase.Parameter>
        get() = _navigateToMachineResult

    init {
        invokeMediatorUseCase(
            useCase = loadMachineListUseCase,
            params = Unit
        )
    }

    override fun onClick(machineId: Int) {
        _navigateToMachineResult.postValue(
            MachineResultNavigationUseCase.Parameter(machineId)
        )
    }

    fun navigateToMachineResult(params: MachineResultNavigationUseCase.Parameter) {
        invokeUseCase(
            useCase = machineResultNavigationUseCase,
            params = params
        )
    }
}