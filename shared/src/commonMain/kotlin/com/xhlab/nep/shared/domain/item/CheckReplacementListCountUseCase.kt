package com.xhlab.nep.shared.domain.item

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseUseCase

@ProvideWithDagger("ItemDomain")
class CheckReplacementListCountUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseUseCase<CheckReplacementListCountUseCase.Parameter, Int>() {

    override suspend fun execute(params: Parameter): Int {
        return elementRepo.getReplacementCountByElement(params.oreDictName)
    }

    data class Parameter(val oreDictName: String)
}
