package com.xhlab.nep.shared.ui.main.machines

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.shared.domain.machine.LoadMachineListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ProvideWithDagger("ViewModel")
class MachineBrowserViewModel constructor(
    loadMachineListUseCase: LoadMachineListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), MachineListener {

    val machineList = loadMachineListUseCase.observeOnlySuccess()

    val isDBLoaded = generalPreference.isDBLoaded

    // MachineId
    private val _navigateToMachineResult = EventFlow<Int>()
    val navigateToMachineResult: Flow<Int>
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
        navigateToMachineResult(machineId)
    }

    private fun navigateToMachineResult(machineId: Int) {
        scope.launch {
            _navigateToMachineResult.emit(machineId)
        }
    }
}
