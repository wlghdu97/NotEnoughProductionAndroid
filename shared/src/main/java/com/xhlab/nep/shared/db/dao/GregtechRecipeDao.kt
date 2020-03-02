package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.GregtechRecipeEntity
import com.xhlab.nep.shared.db.view.RoomGregtechRecipeView
import com.xhlab.nep.shared.db.view.RoomRecipeElementView

@Dao
abstract class GregtechRecipeDao : BaseDao<GregtechRecipeEntity>() {

    @Transaction
    @Query("""
        SELECT element_view.*, gregtech_recipe.amount FROM element_view
        INNER JOIN gregtech_recipe ON gregtech_recipe.recipe_id = :recipeId
        WHERE element_view.id = gregtech_recipe.target_item_id
    """)
    abstract suspend fun getElementListOfRecipe(recipeId: Long): List<RoomRecipeElementView>

    @Transaction
    @Query("""
        SELECT 
        gregtech_recipe.recipe_id,
        recipe_result.amount,
        gregtech_recipe.enabled,
        gregtech_recipe.duration,
        gregtech_recipe.eut,
        gregtech_machine.id AS machine_id,
        gregtech_machine.name AS machine_name
        FROM gregtech_recipe
        INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
        INNER JOIN gregtech_machine ON gregtech_machine.id = :machineId
        WHERE 
        gregtech_recipe.recipe_id = recipe_result.recipe_id AND
        gregtech_recipe.machine_id = gregtech_machine.id
        GROUP BY gregtech_recipe.recipe_id
        ORDER BY recipe_result.amount DESC
    """)
    abstract fun searchRecipeIdByElement(
        elementId: Long,
        machineId: Int
    ): DataSource.Factory<Int, RoomGregtechRecipeView>
}