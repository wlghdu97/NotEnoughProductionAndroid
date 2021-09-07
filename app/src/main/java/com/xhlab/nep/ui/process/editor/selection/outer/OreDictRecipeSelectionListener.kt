package com.xhlab.nep.ui.process.editor.selection.outer

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

interface OreDictRecipeSelectionListener {
    fun onSelectOreDict(
        from: Recipe,
        to: Recipe,
        target: Element,
        ingredient: Element,
        reversed: Boolean
    )
}
