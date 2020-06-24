package com.xhlab.nep.shared.domain.process

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadProcessListUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : MediatorUseCase<Unit, PagedList<ProcessView>>() {

    override fun executeInternal(params: Unit) = liveData<Resource<PagedList<ProcessView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = processRepo.getProcesses()
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    companion object {
        private const val PAGE_SIZE = 5
    }
}