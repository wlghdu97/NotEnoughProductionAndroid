package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.MachineRecipeEntity
import com.xhlab.nep.shared.db.view.RoomMachineRecipeView
import com.xhlab.nep.shared.db.view.RoomRecipeElementView

@Dao
abstract class MachineRecipeDao : BaseDao<MachineRecipeEntity>() {

    @Transaction
    @Query("""
        SELECT element_view.*, machine_recipe.amount, machine_recipe.meta_data FROM element_view
        INNER JOIN machine_recipe ON machine_recipe.recipe_id = :recipeId
        WHERE element_view.id = machine_recipe.target_item_id
        ORDER BY element_view.type, element_view.localized_name ASC
    """)
    abstract suspend fun getElementListOfRecipe(recipeId: Long): List<RoomRecipeElementView>

    @Transaction
    @Query("""
        SELECT 
        machine_recipe.recipe_id,
        machine_recipe.enabled,
        machine_recipe.duration,
        machine_recipe.power_type,
        machine_recipe.ept,
        machine_recipe.meta_data,
        machine.id AS machine_id,
        machine.name AS machine_name
        FROM machine_recipe
        INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
        INNER JOIN machine ON machine.id = :machineId
        WHERE 
        machine_recipe.recipe_id = recipe_result.recipe_id AND
        machine_recipe.machine_id = machine.id
        GROUP BY machine_recipe.recipe_id
        ORDER BY recipe_result.amount DESC
    """)
    abstract fun searchRecipeIdByElement(
        elementId: Long,
        machineId: Int
    ): DataSource.Factory<Int, RoomMachineRecipeView>
}