package com.xhlab.nep.model.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe

class OreChainRecipe(
    oreDictElement: ElementView,
    ingredient: ElementView
) : Recipe {

    private val inputElement = ElementImpl(
        id = ingredient.id,
        localizedName = ingredient.localizedName,
        unlocalizedName = ingredient.unlocalizedName,
        type = ingredient.type,
        metaData = ingredient.metaData
    )

    private val outputElement = ElementImpl(
        id = oreDictElement.id,
        localizedName = oreDictElement.localizedName,
        unlocalizedName = oreDictElement.unlocalizedName,
        type = oreDictElement.type,
        metaData = oreDictElement.metaData
    )

    override fun getInputs(): List<Element> {
        return listOf(inputElement)
    }

    override fun getOutput(): List<Element> {
        return listOf(outputElement)
    }
}