package com.xhlab.nep.model.form.recipes

import com.xhlab.nep.model.form.ElementForm

data class ShapedRecipeForm(
    private val input: List<ElementForm>,
    private val output: ElementForm
) : RecipeForm {
    override fun getInputs() = input
    override fun getOutput() = listOf(output)
}
