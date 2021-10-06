package com.xhlab.nep.shared.domain.item

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadOreDictListUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<Long, PagedList<String>>() {

    override fun executeInternal(params: Long) = liveData<Resource<PagedList<String>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = elementRepo.getOreDictsByElement(params)
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}
