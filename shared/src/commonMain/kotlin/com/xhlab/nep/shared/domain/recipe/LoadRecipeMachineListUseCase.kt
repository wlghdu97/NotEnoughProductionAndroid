package com.xhlab.nep.shared.domain.recipe

import com.xhlab.nep.model.recipes.view.RecipeMachineView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("RecipeDomain")
class LoadRecipeMachineListUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseMediatorUseCase<LoadRecipeMachineListUseCase.Parameter, Pager<Int, RecipeMachineView>>() {

    override suspend fun executeInternal(params: Parameter): Flow<Resource<Pager<Int, RecipeMachineView>>> {
        val pager = elementRepo.getRecipeMachinesByElement(params.elementId)
        return flowOf(Resource.success(pager))
    }

    data class Parameter(val elementId: Long)
}
