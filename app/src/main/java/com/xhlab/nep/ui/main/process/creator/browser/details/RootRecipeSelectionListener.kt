package com.xhlab.nep.ui.main.process.creator.browser.details

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

interface RootRecipeSelectionListener {
    fun onSelect(targetRecipe: Recipe, keyElement: RecipeElement)
}
