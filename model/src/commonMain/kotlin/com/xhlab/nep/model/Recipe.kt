package com.xhlab.nep.model

interface Recipe {
    fun getInputs(): List<RecipeElement>
    fun getOutput(): List<RecipeElement>
}
