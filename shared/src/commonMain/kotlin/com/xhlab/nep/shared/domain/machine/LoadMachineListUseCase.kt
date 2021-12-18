package com.xhlab.nep.shared.domain.machine

import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("MachineDomain")
class LoadMachineListUseCase constructor(
    private val machineRepo: MachineRepo
) : BaseMediatorUseCase<Unit, Pager<Int, Machine>>() {

    override suspend fun executeInternal(params: Unit): Flow<Resource<Pager<Int, Machine>>> {
        val pager = machineRepo.getMachines()
        return flowOf(Resource.success(pager))
    }
}
