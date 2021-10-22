package com.xhlab.nep.ui.process.editor.selection

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.OreDictRecipeSelectionListener

fun ProcessEditViewModel.ConnectionConstraint.select(
    to: Recipe,
    listener: RecipeSelectionListener?,
    keyElement: Element? = getKeyElement(recipe),
    reversed: Boolean = isReversed()
) {
    val from = recipe
    if (keyElement != null) {
        when (connectToParent xor reversed) {
            true -> listener?.onSelect(from, to, element, reversed)
            false -> listener?.onSelect(to, from, element, reversed)
        }
    }
}

fun ProcessEditViewModel.ConnectionConstraint.select(
    to: Recipe,
    targetElementId: Long,
    listener: RecipeSelectionListener?,
    oreDictListener: OreDictRecipeSelectionListener?
) {
    val reversed = isReversed()
    val from = recipe
    val keyElement = getKeyElement(from)
    if (keyElement != null) {
        if (oreDictListener != null &&
            recipe !is OreChainRecipe &&
            element.type == Element.ORE_CHAIN
        ) {
            val ingredient = (to.getInputs() + to.getOutput()).find {
                (it as? ElementView)?.id == targetElementId
            }
            if (ingredient != null) {
                if (ingredient.unlocalizedName != element.unlocalizedName) {
                    when (connectToParent xor reversed) {
                        true ->
                            oreDictListener.onSelectOreDict(from, to, element, ingredient, reversed)
                        false ->
                            oreDictListener.onSelectOreDict(to, from, element, ingredient, reversed)
                    }
                } else {
                    select(to, listener, keyElement, reversed)
                }
            }
        } else {
            select(to, listener, keyElement, reversed)
        }
    }
}

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
