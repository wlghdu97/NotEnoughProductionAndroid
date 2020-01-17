package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeRepoImpl @Inject constructor(
    private val recipeAdder: RecipeAdder
) : RecipeRepo {

    private val io = Dispatchers.IO

    override suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        recipeAdder.insertRecipes(recipes)
    }
}