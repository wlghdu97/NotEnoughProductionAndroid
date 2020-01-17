package com.xhlab.nep.shared.data.recipe

import androidx.room.withTransaction
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.model.recipes.GregtechRecipe
import com.xhlab.nep.shared.data.element.RoomElementMapper
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.*
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

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

        suspend fun insertItemsFromRecipe(recipe: Recipe) {
            val bothItemList = recipe.getDistinctItemList()

            // insert items
            val fullEntities = bothItemList.flatMap { mapper.map(it) }.distinct()
            db.getElementDao().insert(fullEntities)
        }

        for (recipe in recipes) {
            insertItemsFromRecipe(recipe)
        }
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
        val inputIdList = inputPair.map { it.first.getId() }
        val outputIdList = outputPair.map { it.first.getId() }

        when (recipe) {
            is GregtechRecipe -> {
                val recipeList = ArrayList<GregtechRecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    recipeList.add(
                        GregtechRecipeEntity(
                            recipeId = recipeId,
                            targetItemId = inputIdList[index],
                            amount = pair.first.amount * pair.second,
                            machineId = recipe.machineId,
                            isEnabled = recipe.isEnabled,
                            duration = recipe.duration,
                            eut = recipe.eut
                        )
                    )
                }
                db.getGregtechRecipeDao().insert(recipeList)
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
            resultList.add(
                RecipeResultEntity(
                    recipeId = recipeId,
                    resultItemId = outputIdList[index],
                    amount = pair.first.amount * pair.second
                )
            )
        }
        db.getRecipeResultDao().insert(resultList)
    }

    private fun Recipe.getDistinctItemList(): List<Element> {
        return (getInputs() + getOutput()).distinct()
    }

    private suspend fun Element.getId(): Long {
        return when (this) {
            is OreDictElement -> {
                db.getElementDao().getOreDictChainId(oreDictNameList)
            }
            else -> {
                val unlocalizedName = unlocalizedName
                val metaData = (this as? Item)?.metaData?.toString()
                when (metaData.isNullOrEmpty()) {
                    true -> db.getElementDao().getId(unlocalizedName)
                    false -> db.getElementDao().getId(unlocalizedName, metaData)
                }
            }
        }
    }

    private fun generateLongUUID() = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
}