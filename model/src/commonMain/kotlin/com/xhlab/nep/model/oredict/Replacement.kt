package com.xhlab.nep.model.oredict

import com.xhlab.nep.model.RecipeElement

data class Replacement(
    val oreDictName: String,
    val elementList: List<RecipeElement>
)
