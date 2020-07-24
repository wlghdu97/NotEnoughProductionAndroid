package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.UseCase
import javax.inject.Inject

class LoadElementDetailWithKeyUseCase @Inject constructor(
    private val elementRepo: ElementRepo
) : UseCase<LoadElementDetailWithKeyUseCase.Parameter, List<ElementView>>() {

    override suspend fun execute(params: Parameter): List<ElementView> {
        val ids = elementRepo.getIdsByKey(params.key)
        return if (ids.isEmpty()) {
            throw NullPointerException("element id not found.")
        } else {
            val list = arrayListOf<ElementView>()
            for (id in ids) {
                val elementView = elementRepo.getElementDetail(id)
                if (elementView != null) {
                    list.add(elementView)
                } else {
                    throw NullPointerException("element not found.")
                }
            }
            list
        }
    }

    data class Parameter(val key: String)
}