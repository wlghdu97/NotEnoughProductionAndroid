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
        ORDER BY element_view.type, element_view.localized_name ASC
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

    @Transaction
    @Query("""
        SELECT recipe.recipe_id FROM recipe
        INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
        INNER JOIN element_fts ON element_fts.docid = recipe.target_item_id
        WHERE recipe.recipe_id = recipe_result.recipe_id AND element_fts MATCH :term
        GROUP BY recipe.recipe_id
        ORDER BY recipe.amount
    """)
    abstract fun searchRecipeIdByElement(
        elementId: Long,
        term: String
    ): DataSource.Factory<Int, RoomCraftingRecipeView>

    @Transaction
    @Query("""
        SELECT recipe.recipe_id FROM recipe
        WHERE recipe.target_item_id = :elementId
        GROUP BY recipe.recipe_id
        ORDER BY recipe.amount
    """)
    abstract fun searchUsageRecipeIdByElement(
        elementId: Long
    ): DataSource.Factory<Int, RoomCraftingRecipeView>
}