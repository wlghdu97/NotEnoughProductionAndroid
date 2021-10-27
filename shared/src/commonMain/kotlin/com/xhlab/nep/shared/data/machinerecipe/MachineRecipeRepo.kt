package com.xhlab.nep.shared.data.machinerecipe

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView

interface MachineRecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView>
    suspend fun searchRecipeByElement(
        elementId: Long,
        machineId: Int,
        term: String
    ): Pager<Int, RecipeView>

    suspend fun searchUsageRecipeByElement(
        elementId: Long,
        machineId: Int,
        term: String
    ): Pager<Int, RecipeView>
}
