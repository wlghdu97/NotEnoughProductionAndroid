package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseUseCase

@ProvideWithDagger("ItemDomain")
class LoadElementDetailUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseUseCase<Long, RecipeElement>() {

    override suspend fun execute(params: Long): RecipeElement {
        return elementRepo.getElementDetail(params)
            ?: throw NullPointerException("element not found.")
    }
}
