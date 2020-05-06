package com.xhlab.nep.shared.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.RecipeResultEntity
import com.xhlab.nep.shared.db.view.RoomRecipeElementView

@Dao
abstract class RecipeResultDao : BaseDao<RecipeResultEntity>() {

    @Transaction
    @Query("""
        SELECT element_view.*, recipe_result.amount, recipe_result.meta_data FROM element_view
        INNER JOIN recipe_result ON recipe_result.recipe_id = :recipeId
        WHERE element_view.id = recipe_result.result_item_id
    """)
    abstract suspend fun getElementListOfResult(recipeId: Long): List<RoomRecipeElementView>
}