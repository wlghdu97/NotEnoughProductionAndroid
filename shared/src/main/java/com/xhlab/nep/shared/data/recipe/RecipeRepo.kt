package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.Recipe

interface RecipeRepo {
    suspend fun insertRecipes(recipes: List<Recipe>)
}