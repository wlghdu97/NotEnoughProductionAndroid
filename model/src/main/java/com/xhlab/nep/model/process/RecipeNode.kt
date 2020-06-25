package com.xhlab.nep.model.process

import com.xhlab.nep.model.Recipe

data class RecipeNode<R : Recipe>(val recipe: R, val childNodes: List<RecipeNode<R>>) {

    fun getNodeCount(): Int {
        return 1 + childNodes.sumBy { it.getNodeCount() }
    }
}