package com.xhlab.nep.shared.data.machinerecipe

import androidx.paging.DataSource
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView

interface MachineRecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    fun searchRecipeByElement(elementId: Long, machineId: Int): DataSource.Factory<Int, RecipeView>
}