package com.xhlab.nep.model.process.recipes

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe

class SupplierRecipe(element: ElementView) : Recipe {
    private val innerElement: Element = ElementImpl(
        id = element.id,
        localizedName = element.localizedName,
        unlocalizedName = element.unlocalizedName,
        type = element.type,
        metaData = element.metaData
    )

    override fun getInputs(): List<Element> {
        return emptyList()
    }

    override fun getOutput(): List<Element> {
        return listOf(innerElement)
    }
}