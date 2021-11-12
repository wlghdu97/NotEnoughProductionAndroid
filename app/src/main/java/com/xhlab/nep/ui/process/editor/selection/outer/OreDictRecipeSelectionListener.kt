package com.xhlab.nep.ui.process.editor.selection.outer

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement

interface OreDictRecipeSelectionListener {
    fun onSelectOreDict(
        from: Recipe,
        to: Recipe,
        target: RecipeElement,
        ingredient: RecipeElement,
        reversed: Boolean
    )
}
