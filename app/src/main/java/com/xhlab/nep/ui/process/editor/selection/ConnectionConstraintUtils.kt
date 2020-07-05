package com.xhlab.nep.ui.process.editor.selection

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel

fun ProcessEditViewModel.ConnectionConstraint.isReversed(): Boolean {
    val keyInOutput = recipe.getOutput().find { it.unlocalizedName == elementKey } != null
    return connectToParent xor keyInOutput
}

fun ProcessEditViewModel.ConnectionConstraint.getKeyElement(
    node: Recipe,
    isReversed: Boolean? = isReversed()
): Element? {
    return if (connectToParent xor (isReversed == true)) {
        node.getInputs().find { it.unlocalizedName == elementKey }
    } else {
        node.getOutput().find { it.unlocalizedName == elementKey }
    }
}