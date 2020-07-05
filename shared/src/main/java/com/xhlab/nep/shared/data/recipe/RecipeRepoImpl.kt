package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RecipeRepoImpl @Inject constructor(
    private val db: AppDatabase,
    private val recipeAdder: RecipeAdder
) : RecipeRepo {

    private val io = Dispatchers.IO

    override suspend fun getElementListByRecipe(recipeId: Long) = withContext(io) {
        db.getRecipeDao().getElementListOfRecipe(recipeId)
    }

    override fun searchRecipeByElement(elementId: Long)
            = db.getRecipeDao().searchRecipeIdByElement(elementId).map {
        runBlocking {
            it.also {
                it.itemList = db.getRecipeDao().getElementListOfRecipe(it.recipeId)
                it.resultItemList = db.getRecipeResultDao().getElementListOfResult(it.recipeId)
            } as RecipeView
        }
    }

    override fun searchUsageRecipeByElement(elementId: Long)
            = db.getRecipeDao().searchUsageRecipeIdByElement(elementId).map {
        runBlocking {
            it.also {
                it.itemList = db.getRecipeResultDao().getElementListOfResult(it.recipeId)
                it.resultItemList = db.getRecipeDao().getElementListOfRecipe(it.recipeId)
            } as RecipeView
        }
    }

    override suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        recipeAdder.insertRecipes(recipes)
    }
}