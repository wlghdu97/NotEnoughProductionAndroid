package com.xhlab.nep.shared.domain.item

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.domain.BaseUseCase
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger

@ProvideWithDagger("ItemDomain")
class LoadElementDetailWithKeyUseCase constructor(
    private val elementRepo: ElementRepo
) : BaseUseCase<LoadElementDetailWithKeyUseCase.Parameter, List<RecipeElement>>() {

    override suspend fun execute(params: Parameter): List<RecipeElement> {
        val ids = elementRepo.getIdsByKey(params.key)
        return if (ids.isEmpty()) {
            throw NullPointerException("element id not found.")
        } else {
            val list = arrayListOf<RecipeElement>()
            for (id in ids) {
                val element = elementRepo.getElementDetail(id)
                if (element != null) {
                    list.add(element)
                } else {
                    throw NullPointerException("element not found.")
                }
            }
            list
        }
    }

    data class Parameter(val key: String)
}
