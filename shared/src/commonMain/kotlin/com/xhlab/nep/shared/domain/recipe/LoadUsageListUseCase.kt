package com.xhlab.nep.shared.domain.recipe

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ProvideWithDagger("RecipeDomain")
class LoadUsageListUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<Long, Pager<Int, ElementView>>() {

    override suspend fun executeInternal(params: Long): Flow<Resource<Pager<Int, ElementView>>> {
        val pager = elementRepo.getUsagesByElement(params)
        return flowOf(Resource.success(pager))
    }
}
