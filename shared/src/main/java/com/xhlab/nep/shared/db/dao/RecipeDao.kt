package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.RecipeEntity
import com.xhlab.nep.shared.db.view.RoomCraftingRecipeView
import com.xhlab.nep.shared.db.view.RoomRecipeElementView

@Dao
abstract class RecipeDao : BaseDao<RecipeEntity>() {

    @Transaction
    @Query("""
        SELECT element_view.*, recipe.amount FROM element_view
        INNER JOIN recipe ON recipe.recipe_id = :recipeId
        WHERE element_view.id = recipe.target_item_id
    """)
    abstract suspend fun getElementListOfRecipe(recipeId: Long): List<RoomRecipeElementView>

    @Transaction
    @Query("""
        SELECT recipe.recipe_id FROM recipe
        INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
        WHERE recipe.recipe_id = recipe_result.recipe_id
        GROUP BY recipe.recipe_id
        ORDER BY recipe.amount
    """)
    abstract fun searchRecipeIdByElement(
        elementId: Long
    ): DataSource.Factory<Int, RoomCraftingRecipeView>
}