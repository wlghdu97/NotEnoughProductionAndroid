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
) : MediatorUseCase<ElementSearchUseCase.Parameter, PagedList<ElementView>>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<PagedList<ElementView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()

        val dataSource = when {
            params.term.isEmpty() -> elementRepo.getElements()
            else -> elementRepo.searchByName("*${params.term}*")
        }
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val term: String)

    companion object {
        private const val PAGE_SIZE = 10
    }
}