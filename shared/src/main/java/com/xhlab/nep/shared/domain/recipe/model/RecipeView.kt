package com.xhlab.nep.shared.domain.recipe.model

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

abstract class RecipeView : Recipe {
    abstract val recipeId: Long
    abstract val itemList: List<RecipeElementView>
    abstract val resultItemList: List<RecipeElementView>

    override fun getInputs(): List<Element> {
        return itemList
    }

    override fun getOutput(): List<Element> {
        return resultItemList
    }
}