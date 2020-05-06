package com.xhlab.nep.shared.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.xhlab.nep.shared.db.BaseDao
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.view.RoomElementView
import com.xhlab.nep.shared.domain.recipe.model.RecipeMachineView

@Dao
abstract class ElementDao : BaseDao<ElementEntity>() {

    // TODO clean this
    @Transaction
    @Query("""
        SELECT
        machines.id AS machineId, 
        machines.name AS machineName, 
        machines.mod_name AS modName,
        machines.recipeCount AS recipeCount
        FROM (
            SELECT machine.id, machine.name, machine.mod_name,
            COUNT(DISTINCT machine_recipe.recipe_id) AS recipeCount FROM machine_recipe
            INNER JOIN machine ON machine.id = machine_recipe.machine_id
            INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
            WHERE machine_recipe.recipe_id = recipe_result.recipe_id
            GROUP BY machine.name
            UNION ALL
            SELECT -1 AS id, "Crafting Table" AS name, "minecraft" AS mod_name,
            COUNT(DISTINCT recipe.recipe_id) AS recipeCount FROM recipe
            INNER JOIN recipe_result ON recipe_result.result_item_id = :elementId
            WHERE recipe.recipe_id = recipe_result.recipe_id
            GROUP BY name
        ) AS machines
    """)
    abstract fun getMachinesByElement(elementId: Long): DataSource.Factory<Int, RecipeMachineView>

    @Transaction
    @Query("""
        SELECT * FROM element_view
        WHERE element_view.type NOT IN (2, 3)
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun getElements(): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT * FROM element_view
        INNER JOIN element_fts ON element_view.id = element_fts.docid
        WHERE element_fts MATCH :term AND element_view.type NOT IN (2, 3)
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun searchByName(term: String): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT DISTINCT element_view.* FROM element_view
        INNER JOIN recipe
        INNER JOIN recipe_result ON element_view.id = recipe_result.result_item_id
        GROUP BY element_view.id
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun getCraftingResults(): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT DISTINCT element_view.* FROM element_view
        INNER JOIN machine_recipe ON machine_recipe.machine_id = :machineId
        INNER JOIN recipe_result ON
        element_view.id = recipe_result.result_item_id AND 
        recipe_result.recipe_id = machine_recipe.recipe_id
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun getMachineResults(machineId: Int): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT DISTINCT element_view.* FROM element_view
        INNER JOIN machine_recipe ON machine_recipe.machine_id = :machineId
        INNER JOIN recipe_result ON
        element_view.id = recipe_result.result_item_id AND 
        recipe_result.recipe_id = machine_recipe.recipe_id
        WHERE element_view.localized_name LIKE :term
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun searchMachineResults(machineId: Int, term: String): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT DISTINCT element_view.* FROM element_view
        INNER JOIN machine_recipe ON machine_recipe.machine_id = :machineId
        INNER JOIN recipe_result ON
        element_view.id = recipe_result.result_item_id AND 
        recipe_result.recipe_id = machine_recipe.recipe_id
        INNER JOIN element_fts ON element_view.id = element_fts.docid
        WHERE element_fts MATCH :term
        ORDER BY element_view.localized_name ASC
    """)
    abstract fun searchMachineResultsFts(machineId: Int, term: String): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT element_view.* FROM element_view
        INNER JOIN recipe_result ON recipe_result.recipe_id IN (
            SELECT machine_recipe.recipe_id FROM machine_recipe
            WHERE machine_recipe.target_item_id = :elementId
            UNION ALL
            SELECT recipe.recipe_id FROM recipe
            WHERE recipe.target_item_id = :elementId
        )
        WHERE id = recipe_result.result_item_id
        GROUP BY id
        ORDER BY localized_name ASC
    """)
    abstract fun getUsagesByElement(elementId: Long): DataSource.Factory<Int, RoomElementView>

    @Transaction
    @Query("""
        SELECT element.unlocalized_name AS ore_dict_name FROM element
        INNER JOIN ore_dict_chain ON ore_dict_chain.chain_element_id = :elementId
        WHERE element.id = ore_dict_chain.element_id
    """)
    abstract fun getOreDictsByElement(elementId: Long): DataSource.Factory<Int, String>

    @Transaction
    @Query("""
        SELECT element_view.* FROM element_view
        INNER JOIN replacement ON replacement.name = :oreDictName
        WHERE element_view.id = replacement.element_id
    """)
    abstract fun getReplacementList(oreDictName: String): DataSource.Factory<Int, RoomElementView>

    @Query("""
        SELECT * FROM element_view
        WHERE element_view.id = :id
    """)
    abstract suspend fun getElementDetail(id: Long): RoomElementView?

    @Query("""
        SELECT element.id FROM element
        WHERE element.unlocalized_name = :unlocalizedName
    """)
    abstract suspend fun getId(unlocalizedName: String): Long

    @Query("""
        SELECT chain_element_id FROM ore_dict_chain
        INNER JOIN element ON element.unlocalized_name IN (:unlocalizedNameList)
        WHERE element_id = element.id
    """)
    abstract suspend fun getOreDictChainId(unlocalizedNameList: List<String>): Long

    @Query("""
        DELETE FROM element
    """)
    abstract suspend fun deleteAll()
}