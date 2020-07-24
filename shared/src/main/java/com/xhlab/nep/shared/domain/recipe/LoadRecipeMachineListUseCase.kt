package com.xhlab.nep.shared.domain.recipe

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.model.recipes.view.RecipeMachineView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadRecipeMachineListUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : MediatorUseCase<LoadRecipeMachineListUseCase.Parameter, PagedList<RecipeMachineView>>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<PagedList<RecipeMachineView>>> {
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = elementRepo.getRecipeMachinesByElement(params.elementId)
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val elementId: Long)

    companion object {
        private const val PAGE_SIZE = 10
    }
}