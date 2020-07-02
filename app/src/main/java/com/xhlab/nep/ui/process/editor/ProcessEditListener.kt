package com.xhlab.nep.ui.process.editor

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

interface ProcessEditListener {
    fun onDisconnect(from: Recipe, to: Recipe, element: Element, reversed: Boolean = false)
    fun onMarkNotConsumed(recipe: Recipe, element: Element, consumed: Boolean)
}