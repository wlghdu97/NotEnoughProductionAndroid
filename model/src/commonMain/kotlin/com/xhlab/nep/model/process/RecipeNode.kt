package com.xhlab.nep.model.process

import com.xhlab.nep.model.Recipe

data class RecipeNode(val recipe: Recipe, val childNodes: List<RecipeNode>) {

    fun getNodeCount(): Int {
        return 1 + childNodes.sumBy { it.getNodeCount() }
    }
}
