package com.xhlab.nep.shared.data.recipe

import androidx.room.withTransaction
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.shared.data.element.RoomElementMapper
import com.xhlab.nep.shared.data.generateLongUUID
import com.xhlab.nep.shared.data.getId
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.*
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeAdder @Inject constructor(
    private val db: AppDatabase,
    private val mapper: RoomElementMapper
) {
    private val io = Dispatchers.IO

    suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        insertItemsFromRecipes(recipes)
        insertOreDictChains(recipes)
        insertRecipesInternal(recipes)
    }

    private suspend fun insertItemsFromRecipes(recipes: List<Recipe>) = db.withTransaction {

        fun getItemsFromRecipe(recipe: Recipe): List<ElementEntity> {
            val bothItemList = recipe.getDistinctItemList()
            return bothItemList.flatMap { mapper.map(it) }
        }

        val entitySet = Sequence { recipes.iterator() }
            .flatMap { getItemsFromRecipe(it).asSequence() }
            .distinctBy { it.unlocalizedName }

        db.getElementDao().insert(entitySet.toList())
    }

    private suspend fun insertOreDictChains(recipes: List<Recipe>) = db.withTransaction {
        val oreDictElementList = recipes.asSequence()
            .flatMap { it.getDistinctItemList().asSequence() }
            .filterIsInstance(OreDictElement::class.java)
            .toSet()

        val idByName = oreDictElementList
            .flatMap { it.oreDictNameList }
            .distinct()
            .map { it to db.getElementDao().getId(it) }
            .toMap()

        val elementEntities = ArrayList<ElementEntity>()
        val chainEntities = oreDictElementList.flatMap {
            val chainId = generateLongUUID()
            elementEntities.add(
                ElementEntity(
                    id = chainId,
                    unlocalizedName = chainId.toString(),
                    localizedName = "",
                    type = ORE_CHAIN
                )
            )
            it.oreDictNameList.map { name ->
                OreDictChainEntity(
                    chainId = chainId,
                    elementId = idByName[name]
                        ?: throw NullPointerException("ore dict name not found.")
                )
            }
        }

        db.getElementDao().insert(elementEntities)
        db.getOreDictChainDao().insert(chainEntities)
    }

    private suspend fun insertRecipesInternal(recipes: List<Recipe>) = db.withTransaction {
        for (recipe in recipes) {
            insertRecipe(recipe)
        }
    }

    private suspend fun insertRecipe(recipe: Recipe) {

        fun List<Element>.toItemAmountPair(): List<Pair<Element, Int>> {
            return this.asSequence()
                .groupBy { it.unlocalizedName }
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
                val recipeList = ArrayList<MachineRecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    val metaData = if (pair.first is Item) {
                        (pair.first as Item).metaData
                    } else null
                    recipeList.add(
                        MachineRecipeEntity(
                            recipeId = recipeId,
                            targetItemId = inputIdList[index],
                            amount = pair.first.amount * pair.second,
                            machineId = recipe.machineId,
                            isEnabled = recipe.isEnabled,
                            duration = recipe.duration,
                            powerType = recipe.powerType,
                            ept = recipe.ept,
                            metaData = metaData
                        )
                    )
                }
                db.getMachineRecipeDao().insert(recipeList)
            }
            else -> {
                val recipeList = ArrayList<RecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    recipeList.add(
                        RecipeEntity(
                            recipeId = recipeId,
                            targetItemId = inputIdList[index],
                            amount = pair.second
                        )
                    )
                }
                db.getRecipeDao().insert(recipeList)
            }
        }

        val resultList = ArrayList<RecipeResultEntity>()
        for ((index, pair) in outputPair.withIndex()) {
            val metaData = if (pair.first is Item) {
                (pair.first as Item).metaData
            } else null
            resultList.add(
                RecipeResultEntity(
                    recipeId = recipeId,
                    resultItemId = outputIdList[index],
                    amount = pair.first.amount * pair.second,
                    metaData = metaData
                )
            )
        }
        db.getRecipeResultDao().insert(resultList)
    }

    private fun Recipe.getDistinctItemList(): List<Element> {
        return (getInputs() + getOutput()).distinct()
    }
}
