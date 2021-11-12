package com.xhlab.nep.model.process.recipes

import com.xhlab.nep.model.PlainRecipeElement
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

class SupplierRecipe(element: RecipeElement) : Recipe {

    private val innerElement: RecipeElement = PlainRecipeElement(
        id = element.id,
        localizedName = element.localizedName,
        unlocalizedName = element.unlocalizedName,
        type = element.type,
        metaData = element.metaData,
        amount = element.amount
    )

    override fun getInputs(): List<RecipeElement> {
        return emptyList()
    }

    override fun getOutput(): List<RecipeElement> {
        return listOf(innerElement)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is SupplierRecipe) return false
        return (getOutput()[0].unlocalizedName == other.getOutput()[0].unlocalizedName)
    }

    override fun hashCode(): Int {
        return (getOutput()[0].unlocalizedName.hashCode())
    }
}
