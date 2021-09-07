package com.xhlab.nep.ui.main.process.creator.browser.details

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

interface RootRecipeSelectionListener {
    fun onSelect(targetRecipe: Recipe, keyElement: Element)
}
