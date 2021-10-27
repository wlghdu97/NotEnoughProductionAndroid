package com.xhlab.nep.shared.domain.machine

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ProvideWithDagger("MachineDomain")
class MachineResultSearchUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<MachineResultSearchUseCase.Parameters, Pager<Int, ElementView>>() {

    override suspend fun executeInternal(params: Parameters): Flow<Resource<Pager<Int, ElementView>>> {
        val pager = when {
            params.term.isEmpty() -> elementRepo.getResultsByMachine(params.machineId)
            params.term.length < 3 -> elementRepo.searchMachineResults(
                machineId = params.machineId,
                term = "%${params.term}%"
            )
            else -> elementRepo.searchMachineResultsFts(params.machineId, "*${params.term}*")
        }
        return flowOf(Resource.success(pager))
    }

    data class Parameters(val machineId: Int, val term: String = "")
}
