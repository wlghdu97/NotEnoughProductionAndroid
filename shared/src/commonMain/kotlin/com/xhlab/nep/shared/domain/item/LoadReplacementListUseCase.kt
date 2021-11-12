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
class LoadReplacementListUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<String, Pager<Int, RecipeElement>>() {

    override suspend fun executeInternal(params: String): Flow<Resource<Pager<Int, RecipeElement>>> {
        val pager = elementRepo.getReplacementsByElement(params)
        return flowOf(Resource.success(pager))
    }
}
