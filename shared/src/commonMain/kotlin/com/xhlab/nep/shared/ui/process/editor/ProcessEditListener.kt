package com.xhlab.nep.shared.ui.process.editor

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

interface ProcessEditListener {
    fun onConnectToParent(recipe: Recipe, element: RecipeElement, degree: Int)
    fun onConnectToChild(recipe: Recipe, element: RecipeElement, degree: Int)
    fun onDisconnect(from: Recipe, to: Recipe, element: RecipeElement, reversed: Boolean = false)
    fun onMarkNotConsumed(recipe: Recipe, element: RecipeElement, consumed: Boolean)
}
