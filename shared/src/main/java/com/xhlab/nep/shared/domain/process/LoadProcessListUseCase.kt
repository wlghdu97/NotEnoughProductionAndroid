package com.xhlab.nep.shared.domain.process

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadProcessListUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : MediatorUseCase<LoadProcessListUseCase.Parameter, PagedList<ProcessSummary>>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<PagedList<ProcessSummary>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = when (params.targetElementKey.isNullOrEmpty()) {
            true -> processRepo.getProcesses()
            false -> processRepo.getProcessesByTarget(params.targetElementKey)
        }
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val targetElementKey: String? = null)

    companion object {
        private const val PAGE_SIZE = 5
    }
}
