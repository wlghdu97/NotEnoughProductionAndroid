package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("ItemDomain")
class LoadOreDictListUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<Long, Pager<Int, String>>() {

    override suspend fun executeInternal(params: Long): Flow<Resource<Pager<Int, String>>> {
        val pager = elementRepo.getOreDictsByElement(params)
        return flowOf(Resource.success(pager))
    }
}
