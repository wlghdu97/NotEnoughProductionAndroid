package com.xhlab.nep.shared.domain.machine

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class MachineResultSearchUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<MachineResultSearchUseCase.Parameters, PagedList<ElementView>>() {

    override fun executeInternal(params: Parameters) = liveData<Resource<PagedList<ElementView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()

        val dataSource = when {
            params.term.isEmpty() -> elementRepo.getResultsByMachine(params.machineId)
            params.term.length < 3 -> elementRepo.searchMachineResults(params.machineId, "%${params.term}%")
            else -> elementRepo.searchMachineResultsFts(params.machineId, "*${params.term}*")
        }
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameters(val machineId: Int, val term: String = "")

    companion object {
        private const val PAGE_SIZE = 10
    }
}