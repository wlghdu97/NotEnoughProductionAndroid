package com.xhlab.nep.shared.data.recipe

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.shared.data.element.SqlDelightElementMapper
import com.xhlab.nep.shared.data.generateLongUUID
import com.xhlab.nep.shared.data.getId
import com.xhlab.nep.shared.db.Machine_recipe
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.db.Recipe_result
import com.xhlab.nep.shared.db.entity.ElementType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Singleton
import com.xhlab.nep.shared.db.Element as ElementEntity
import com.xhlab.nep.shared.db.Recipe as RecipeEntity

@Singleton
class RecipeAdder constructor(private val db: Nep) {

    private val mapper = SqlDelightElementMapper()
    private val io = Dispatchers.IO

    suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        insertItemsFromRecipes(recipes)
        insertOreDictChains(recipes)
        insertRecipesInternal(recipes)
    }

    private fun insertItemsFromRecipes(recipes: List<Recipe>) {
        db.elementQueries.transaction {

            fun getItemsFromRecipe(recipe: Recipe): List<ElementEntity> {
                val bothItemList = recipe.getDistinctItemList()
                return bothItemList.flatMap { mapper.map(it) }
            }

            val entitySet = Sequence { recipes.iterator() }
                .flatMap { getItemsFromRecipe(it).asSequence() }
                .distinctBy { it.unlocalized_name }

            for (entity in entitySet) {
                db.elementQueries.insert(entity)
            }
        }
    }

    private fun insertOreDictChains(recipes: List<Recipe>) {
        db.elementQueries.transaction {
            val oreDictElementList = recipes.asSequence()
                .flatMap { it.getDistinctItemList().asSequence() }
                .filterIsInstance(OreDictElement::class.java)
                .toSet()

            val idByName = oreDictElementList
                .flatMap { it.oreDictNameList }
                .distinct()
                .map { it to db.elementQueries.getIds(it).executeAsList().first() }
                .toMap()

            val elementEntities = ArrayList<ElementEntity>()
            val chainEntities = oreDictElementList.flatMap {
                val chainId = generateLongUUID()
                elementEntities.add(
                    ElementEntity(
                        id = chainId,
                        unlocalized_name = chainId.toString(),
                        localized_name = "",
                        type = ElementType.ORE_CHAIN.index
                    )
                )
                it.oreDictNameList.map { name ->
                    chainId to (idByName[name]
                        ?: throw NullPointerException("ore dict name not found."))
                }
            }

            for (element in elementEntities) {
                db.elementQueries.insert(element)
            }
            for ((chainId, elementId) in chainEntities) {
                db.oreDictChainQueries.insert(chainId, elementId)
            }
        }
    }

    private fun insertRecipesInternal(recipes: List<Recipe>) {
        db.elementQueries.transaction {
            for (recipe in recipes) {
                insertRecipe(recipe)
            }
        }
    }

    private fun insertRecipe(recipe: Recipe) {

        fun List<Element>.toItemAmountPair(): List<Pair<Element, Int>> {
            return this.groupBy { it.unlocalizedName }
                .map { it.value[0] to it.value.size }
                .toList()
        }

        val inputPair = recipe.getInputs().toItemAmountPair()
        val outputPair = recipe.getOutput().toItemAmountPair()

        // insert recipes
        val recipeId = generateLongUUID()
        val inputIdList = inputPair.map { it.first.getId(db) }
        val outputIdList = outputPair.map { it.first.getId(db) }

        when (recipe) {
            is MachineRecipe -> {
                val recipeList = ArrayList<Machine_recipe>()
                for ((index, pair) in inputPair.withIndex()) {
                    val metaData = if (pair.first is Item) {
                        (pair.first as Item).metaData
                    } else null
                    recipeList.add(
                        Machine_recipe(
                            recipe_id = recipeId,
                            target_item_id = inputIdList[index],
                            amount = pair.first.amount * pair.second,
                            machine_id = recipe.machineId.toLong(),
                            enabled = recipe.isEnabled,
                            duration = recipe.duration,
                            power_type = recipe.powerType,
                            ept = recipe.ept,
                            meta_data = metaData
                        )
                    )
                }

                for (entity in recipeList) {
                    db.machineRecipeQueries.insert(entity)
                }
            }
            else -> {
                val recipeList = ArrayList<RecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    recipeList.add(
                        RecipeEntity(
                            recipe_id = recipeId,
                            target_item_id = inputIdList[index],
                            amount = pair.second
                        )
                    )
                }

                for (entity in recipeList) {
                    db.recipeQueries.insert(entity)
                }
            }
        }

        val resultList = ArrayList<Recipe_result>()
        for ((index, pair) in outputPair.withIndex()) {
            val metaData = if (pair.first is Item) {
                (pair.first as Item).metaData
            } else null
            resultList.add(
                Recipe_result(
                    recipe_id = recipeId,
                    result_item_id = outputIdList[index],
                    amount = pair.first.amount * pair.second,
                    meta_data = metaData
                )
            )
        }

        for (entity in resultList) {
            db.recipeResultQueries.insert(entity)
        }
    }

    private fun Recipe.getDistinctItemList(): List<Element> {
        return (getInputs() + getOutput()).distinct()
    }
}
