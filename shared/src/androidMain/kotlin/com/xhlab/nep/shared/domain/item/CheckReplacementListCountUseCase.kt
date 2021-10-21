package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.UseCase
import javax.inject.Inject

class CheckReplacementListCountUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : UseCase<CheckReplacementListCountUseCase.Parameter, Int>() {

    override suspend fun execute(params: Parameter): Int {
        return elementRepo.getReplacementCountByElement(params.oreDictName)
    }

    data class Parameter(val oreDictName: String)
}
