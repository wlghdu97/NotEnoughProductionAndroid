package com.xhlab.nep.ui.main.machines

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.MachineResultNavigationUseCase
import com.xhlab.nep.shared.domain.machine.LoadMachineListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MachineBrowserViewModel @Inject constructor(
    loadMachineListUseCase: LoadMachineListUseCase,
    generalPreference: GeneralPreference,
    private val machineResultNavigationUseCase: MachineResultNavigationUseCase
) : ViewModel(), MachineListener {

    val machineList = loadMachineListUseCase.observeOnlySuccess()

    val isDBLoaded = generalPreference.isDBLoaded

    private val _navigateToMachineResult = EventFlow<MachineResultNavigationUseCase.Parameter>()
    val navigateToMachineResult: Flow<MachineResultNavigationUseCase.Parameter>
        get() = _navigateToMachineResult.flow

    init {
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadMachineListUseCase,
                params = Unit
            )
        }
    }

    override fun onClick(machineId: Int) {
        scope.launch {
            _navigateToMachineResult.emit(
                MachineResultNavigationUseCase.Parameter(machineId)
            )
        }
    }

    fun navigateToMachineResult(params: MachineResultNavigationUseCase.Parameter) {
        invokeUseCase(
            useCase = machineResultNavigationUseCase,
            params = params
        )
    }
}
