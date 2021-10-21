package com.xhlab.nep.shared.data.recipe

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView

interface RecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    suspend fun insertRecipes(recipes: List<Recipe>)
    suspend fun searchRecipeByElement(elementId: Long, term: String): Pager<Int, RecipeView>
    suspend fun searchUsageRecipeByElement(elementId: Long, term: String): Pager<Int, RecipeView>
}
