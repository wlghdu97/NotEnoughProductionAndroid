package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadReplacementListUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<String, Pager<Int, ElementView>>() {

    override suspend fun executeInternal(params: String): Flow<Resource<Pager<Int, ElementView>>> {
        val pager = elementRepo.getReplacementsByElement(params)
        return flowOf(Resource.success(pager))
    }
}
