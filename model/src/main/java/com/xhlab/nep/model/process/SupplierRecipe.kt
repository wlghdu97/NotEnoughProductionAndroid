package com.xhlab.nep.model.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

class SupplierRecipe(element: Element) : Recipe {
    private val innerElement: Element = ElementImpl(element.localizedName, element.unlocalizedName)

    override fun getInputs(): List<Element> {
        return emptyList()
    }

    override fun getOutput(): List<Element> {
        return listOf(innerElement)
    }

    data class ElementImpl(
        override val localizedName: String,
        override val unlocalizedName: String
    ) : Element() {
        override val amount: Int = 1
    }
}