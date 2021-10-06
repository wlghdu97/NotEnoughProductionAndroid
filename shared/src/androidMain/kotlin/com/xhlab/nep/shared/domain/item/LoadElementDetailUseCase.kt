package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.model.ElementView
import javax.inject.Inject

class LoadElementDetailUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : UseCase<Long, ElementView>() {

    override suspend fun execute(params: Long): ElementView {
        return elementRepo.getElementDetail(params)
            ?: throw NullPointerException("element not found.")
    }
}
