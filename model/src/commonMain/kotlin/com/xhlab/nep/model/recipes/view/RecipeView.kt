package com.xhlab.nep.model.recipes.view

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import kotlinx.serialization.Serializable

@Serializable
abstract class RecipeView : Recipe {
    abstract val recipeId: Long
    abstract val itemList: List<RecipeElement>
    abstract val resultItemList: List<RecipeElement>

    override fun getInputs(): List<RecipeElement> {
        return itemList
    }

    override fun getOutput(): List<RecipeElement> {
        return resultItemList
    }
}
