package com.xhlab.nep.shared.data.recipe

import androidx.paging.DataSource
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView
import com.xhlab.nep.shared.domain.recipe.model.RecipeView

interface RecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    suspend fun insertRecipes(recipes: List<Recipe>)
    fun searchRecipeByElement(elementId: Long): DataSource.Factory<Int, RecipeView>
}