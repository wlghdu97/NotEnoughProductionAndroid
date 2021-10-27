package com.xhlab.nep.shared.domain.machine

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.BaseUseCase

@ProvideWithDagger("MachineDomain")
class LoadMachineUseCase constructor(
    private val machineRepo: MachineRepo
) : BaseUseCase<LoadMachineUseCase.Parameter, Machine?>() {

    override suspend fun execute(params: Parameter): Machine {
        return machineRepo.getMachine(params.machineId)
            ?: throw NullPointerException("machine not found.")
    }

    data class Parameter(val machineId: Int)
}
