package com.xhlab.nep.shared.data.gregtech

import androidx.paging.DataSource
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView
import com.xhlab.nep.shared.domain.recipe.model.RecipeView

interface GregtechRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    suspend fun insertGregtechMachine(machineName: String): Int
    suspend fun deleteGregtechMachines()
    fun searchRecipeByElement(elementId: Long, machineId: Int): DataSource.Factory<Int, RecipeView>
}