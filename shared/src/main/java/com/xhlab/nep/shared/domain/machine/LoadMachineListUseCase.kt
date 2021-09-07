package com.xhlab.nep.shared.domain.machine

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadMachineListUseCase @Inject constructor(
    private val machineRepo: MachineRepo
) : MediatorUseCase<Unit, PagedList<Machine>>() {

    override fun executeInternal(params: Unit) = liveData<Resource<PagedList<Machine>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = machineRepo.getMachines()
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
