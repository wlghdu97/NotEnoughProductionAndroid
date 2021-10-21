package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ElementSearchUseCase @Inject internal constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<ElementSearchUseCase.Parameter, Pager<Int, ElementView>>() {

    override suspend fun executeInternal(params: Parameter): Flow<Resource<Pager<Int, ElementView>>> {
        val pager = when {
            params.term.isEmpty() -> elementRepo.getElements()
            else -> elementRepo.searchByName("*${params.term}*")
        }
        return flowOf(Resource.success(pager))
    }

    data class Parameter(val term: String)
}
