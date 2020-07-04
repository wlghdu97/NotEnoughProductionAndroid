package com.xhlab.nep.ui.process.editor.selection

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

interface RecipeSelectionListener {
    fun onSelect(from: Recipe, to: Recipe, element: Element, reversed: Boolean)
}