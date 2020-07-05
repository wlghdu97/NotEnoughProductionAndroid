package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.UseCase
import javax.inject.Inject

class LoadElementDetailWithKeyUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : UseCase<LoadElementDetailWithKeyUseCase.Parameter, ElementView>() {

    override suspend fun execute(params: Parameter): ElementView {
        val elementId = elementRepo.getIdByKey(params.key)
            ?: throw NullPointerException("element id not found.")
        return elementRepo.getElementDetail(elementId)
            ?: throw NullPointerException("element not found.")
    }

    data class Parameter(val key: String)
}