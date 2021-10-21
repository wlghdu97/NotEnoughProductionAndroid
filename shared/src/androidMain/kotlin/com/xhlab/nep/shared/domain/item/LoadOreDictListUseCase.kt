package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadOreDictListUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<Long, Pager<Int, String>>() {

    override suspend fun executeInternal(params: Long): Flow<Resource<Pager<Int, String>>> {
        val pager = elementRepo.getOreDictsByElement(params)
        return flowOf(Resource.success(pager))
    }
}
