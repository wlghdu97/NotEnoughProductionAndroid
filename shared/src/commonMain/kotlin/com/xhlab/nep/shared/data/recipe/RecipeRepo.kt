package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.form.recipes.RecipeForm
import com.xhlab.nep.model.recipes.view.RecipeView
import kr.sparkweb.multiplatform.paging.Pager

interface RecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElement>
    suspend fun insertRecipes(recipes: List<RecipeForm>)
    suspend fun searchRecipeByElement(elementId: Long, term: String): Pager<Int, RecipeView>
    suspend fun searchUsageRecipeByElement(elementId: Long, term: String): Pager<Int, RecipeView>
}
