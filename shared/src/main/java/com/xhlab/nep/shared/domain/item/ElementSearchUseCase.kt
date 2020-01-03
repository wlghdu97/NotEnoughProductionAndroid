package com.xhlab.nep.shared.domain.item

import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.item.model.ElementView
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

class ElementSearchUseCase @Inject internal constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<String, PagedList<ElementView>>() {

    override fun executeInternal(params: String) = liveData(SupervisorJob()) {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = elementRepo.searchByName(params)
        emitSource(LivePagedListBuilder(dataSource, config).build())
    }

    companion object {
        private const val PAGE_SIZE = 10
    }
}