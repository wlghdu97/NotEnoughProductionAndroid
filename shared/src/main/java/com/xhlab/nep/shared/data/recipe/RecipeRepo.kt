package com.xhlab.nep.shared.data.recipe

import androidx.paging.DataSource
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView

interface RecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    suspend fun insertRecipes(recipes: List<Recipe>)
    fun searchRecipeByElement(elementId: Long, term: String): DataSource.Factory<Int, RecipeView>
    fun searchUsageRecipeByElement(elementId: Long, term: String): DataSource.Factory<Int, RecipeView>
}