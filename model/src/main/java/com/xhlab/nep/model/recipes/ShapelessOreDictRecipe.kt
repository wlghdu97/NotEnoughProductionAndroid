package com.xhlab.nep.model.recipes

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe

data class ShapelessOreDictRecipe(
    private val input: List<Element>,
    private val output: Element
) : Recipe {
    override fun getInputs() = input
    override fun getOutput() = listOf(output)
}