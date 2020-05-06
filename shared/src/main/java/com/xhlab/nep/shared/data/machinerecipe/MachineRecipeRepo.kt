package com.xhlab.nep.shared.data.machinerecipe

import androidx.paging.DataSource
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView
import com.xhlab.nep.shared.domain.recipe.model.RecipeView

interface MachineRecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    fun searchRecipeByElement(elementId: Long, machineId: Int): DataSource.Factory<Int, RecipeView>
}