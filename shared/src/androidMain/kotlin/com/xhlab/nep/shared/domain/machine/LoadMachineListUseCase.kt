package com.xhlab.nep.shared.domain.machine

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadMachineListUseCase @Inject constructor(
    private val machineRepo: MachineRepo
) : BaseMediatorUseCase<Unit, Pager<Int, Machine>>() {

    override suspend fun executeInternal(params: Unit): Flow<Resource<Pager<Int, Machine>>> {
        val pager = machineRepo.getMachines()
        return flowOf(Resource.success(pager))
    }
}
