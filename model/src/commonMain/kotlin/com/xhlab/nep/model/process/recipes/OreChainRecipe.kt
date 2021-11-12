package com.xhlab.nep.model.process.recipes

import com.xhlab.nep.model.PlainRecipeElement
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

class OreChainRecipe(
    oreDictElement: RecipeElement,
    ingredient: RecipeElement
) : Recipe {

    private val inputElement = PlainRecipeElement(
        id = ingredient.id,
        localizedName = ingredient.localizedName,
        unlocalizedName = ingredient.unlocalizedName,
        type = ingredient.type,
        metaData = ingredient.metaData,
        amount = 1
    )

    private val outputElement = PlainRecipeElement(
        id = oreDictElement.id,
        localizedName = oreDictElement.localizedName,
        unlocalizedName = oreDictElement.unlocalizedName,
        type = oreDictElement.type,
        metaData = oreDictElement.metaData,
        amount = 1
    )

    override fun getInputs(): List<RecipeElement> {
        return listOf(inputElement)
    }

    override fun getOutput(): List<RecipeElement> {
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
