package com.xhlab.nep.shared.data.machinerecipe

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.recipes.view.RecipeView
import kr.sparkweb.multiplatform.paging.Pager

interface MachineRecipeRepo {
    suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElement>

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
