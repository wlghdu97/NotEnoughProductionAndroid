package com.xhlab.nep.shared.domain.machine

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.item.model.ElementView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadMachineResultListUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<LoadMachineResultListUseCase.Parameter, PagedList<ElementView>>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<PagedList<ElementView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()

        val dataSource = elementRepo.getResultsByStation(params.machineId)
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val machineId: Int)

    companion object {
        private const val PAGE_SIZE = 10
    }
}