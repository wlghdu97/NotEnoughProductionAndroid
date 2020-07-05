package com.xhlab.nep.ui.process.adapters

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode

fun RecipeNode.toDegreeNode(process: Process, degree: Int): RecipeViewDegreeNode {
    val elements = recipe.getOutput() + recipe.getInputs()
    val connectionList = elements.map {
        ElementConnection(
            amount = it.amount,
            unlocalizedName = it.unlocalizedName,
            localizedName = it.localizedName,
            connections = process.getConnectionStatus(recipe, it)
        )
    }
    return RecipeViewDegreeNode(degree, this, connectionList)
}

data class RecipeViewDegreeNode(
    val degree: Int,
    val node: RecipeNode,
    val connectionList: List<ElementConnection>
)