package com.xhlab.nep.shared.ui.process.editor.selection

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

interface RecipeSelectionListener {
    fun onSelect(from: Recipe, to: Recipe, element: RecipeElement, reversed: Boolean)
}
