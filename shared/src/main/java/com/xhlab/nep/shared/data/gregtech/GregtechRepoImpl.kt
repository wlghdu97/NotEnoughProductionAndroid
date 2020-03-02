package com.xhlab.nep.shared.data.gregtech

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.GregtechMachineEntity
import com.xhlab.nep.shared.domain.recipe.model.RecipeView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GregtechRepoImpl @Inject constructor(
    private val db: AppDatabase
) : GregtechRepo {

    private val io = Dispatchers.IO

    override suspend fun getElementListByRecipe(recipeId: Long) = withContext(io) {
        db.getGregtechRecipeDao().getElementListOfRecipe(recipeId)
    }

    override suspend fun insertGregtechMachine(machineName: String) = withContext(io) {
        db.getGregtechMachineDao().insert(GregtechMachineEntity(name = machineName))
        db.getGregtechMachineDao().getId(machineName)
    }

    override suspend fun deleteGregtechMachines() = withContext(io) {
        db.getGregtechMachineDao().deleteAll()
    }

    override fun searchRecipeByElement(elementId: Long, machineId: Int)
            = db.getGregtechRecipeDao().searchRecipeIdByElement(elementId, machineId).map {
        runBlocking {
            it.also {
                it.itemList = db.getGregtechRecipeDao().getElementListOfRecipe(it.recipeId)
                it.resultItemList = db.getRecipeResultDao().getElementListOfResult(it.recipeId)
            } as RecipeView
        }
    }
}