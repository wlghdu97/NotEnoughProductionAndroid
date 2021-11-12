package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ProvideWithDagger("ItemDomain")
class ElementSearchUseCase internal constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<ElementSearchUseCase.Parameter, Pager<Int, RecipeElement>>() {

    override suspend fun executeInternal(params: Parameter): Flow<Resource<Pager<Int, RecipeElement>>> {
        val pager = when {
            params.term.isEmpty() -> elementRepo.getElements()
            else -> elementRepo.searchByName("*${params.term}*")
        }
        return flowOf(Resource.success(pager))
    }

    data class Parameter(val term: String)
}
