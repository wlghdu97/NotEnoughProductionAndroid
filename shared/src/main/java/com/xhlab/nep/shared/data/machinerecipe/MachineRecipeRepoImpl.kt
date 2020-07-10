package com.xhlab.nep.shared.data.machinerecipe

import androidx.paging.DataSource
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MachineRecipeRepoImpl @Inject constructor(
    private val db: AppDatabase
) : MachineRecipeRepo {

    private val io = Dispatchers.IO

    override suspend fun getElementListByRecipe(recipeId: Long) = withContext(io) {
        db.getMachineRecipeDao().getElementListOfRecipe(recipeId)
    }

    override fun searchRecipeByElement(
        elementId: Long,
        machineId: Int,
        term: String
    ): DataSource.Factory<Int, RecipeView> {
        return when (term.isEmpty()) {
            true -> db.getMachineRecipeDao().searchRecipeIdByElement(elementId, machineId)
            false -> db.getMachineRecipeDao().searchRecipeIdByElement(elementId, machineId, "*$term*")
        }.map {
            runBlocking {
                it.also {
                    it.itemList.addAll(db.getMachineRecipeDao().getElementListOfRecipe(it.recipeId))
                    it.resultItemList.addAll(db.getRecipeResultDao().getElementListOfResult(it.recipeId))
                } as RecipeView
            }
        }
    }

    override fun searchUsageRecipeByElement(elementId: Long, machineId: Int)
            = db.getMachineRecipeDao().searchUsageRecipeIdByElement(elementId, machineId).map {
        runBlocking {
            it.also {
                it.itemList.addAll(db.getRecipeResultDao().getElementListOfResult(it.recipeId))
                it.resultItemList.addAll(db.getMachineRecipeDao().getElementListOfRecipe(it.recipeId))
            } as RecipeView
        }
    }
}