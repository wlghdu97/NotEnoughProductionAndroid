package com.xhlab.nep.model.process.recipes

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

    override fun equals(other: Any?): Boolean {
        if (other !is OreChainRecipe) return false
        return (getInputs()[0].unlocalizedName == other.getInputs()[0].unlocalizedName &&
                getOutput()[0].unlocalizedName == other.getOutput()[0].unlocalizedName)
    }

    override fun hashCode(): Int {
        return (getInputs()[0].unlocalizedName.hashCode() +
                getOutput()[0].unlocalizedName.hashCode())
    }
}