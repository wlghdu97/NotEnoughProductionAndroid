package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("ItemDomain")
class LoadReplacementListUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<String, Pager<Int, RecipeElement>>() {

    override suspend fun executeInternal(params: String): Flow<Resource<Pager<Int, RecipeElement>>> {
        val pager = elementRepo.getReplacementsByElement(params)
        return flowOf(Resource.success(pager))
    }
}
