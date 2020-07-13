package com.xhlab.nep.ui.process.editor.selection

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel

fun ProcessEditViewModel.ConnectionConstraint.isReversed(): Boolean {
    val elementKey = element.unlocalizedName
    val keyInOutput = recipe.getOutput().find { it.unlocalizedName == elementKey } != null
    return connectToParent xor keyInOutput
}

fun ProcessEditViewModel.ConnectionConstraint.getKeyElement(
    node: Recipe,
    isReversed: Boolean? = isReversed()
): Element? {
    val elementKey = element.unlocalizedName
    return if (connectToParent xor (isReversed == true)) {
        node.getInputs().find { it.unlocalizedName == elementKey }
    } else {
        node.getOutput().find { it.unlocalizedName == elementKey }
    }
}

fun ProcessEditViewModel.ConnectionConstraint.getKeyElement(node: Recipe): Element? {
    val elementKey = element.unlocalizedName
    return (node.getInputs() + node.getOutput()).find { it.unlocalizedName == elementKey }
}