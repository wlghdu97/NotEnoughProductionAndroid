package com.xhlab.nep.shared.domain.item

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.item.model.ElementView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class ElementSearchUseCase @Inject internal constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<String, PagedList<ElementView>>() {

    override fun executeInternal(params: String) = liveData<Resource<PagedList<ElementView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = elementRepo.searchByName(params)
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}