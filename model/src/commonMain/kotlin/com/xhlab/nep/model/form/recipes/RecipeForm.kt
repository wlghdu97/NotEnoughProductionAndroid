package com.xhlab.nep.model.form.recipes

import com.xhlab.nep.model.form.ElementForm

interface RecipeForm {
    fun getInputs(): List<ElementForm>
    fun getOutput(): List<ElementForm>
}
