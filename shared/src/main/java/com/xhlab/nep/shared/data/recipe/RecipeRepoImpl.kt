package com.xhlab.nep.shared.data.recipe

import androidx.room.withTransaction
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.GregtechRecipe
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.FLUID
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import com.xhlab.nep.shared.db.entity.GregtechRecipeEntity
import com.xhlab.nep.shared.db.entity.RecipeEntity
import com.xhlab.nep.shared.db.entity.RecipeResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

internal class RecipeRepoImpl @Inject constructor(
    private val db: AppDatabase
) : RecipeRepo {

    private val io = Dispatchers.IO

    override suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        insertItems(recipes)
        insertRecipesInternal(recipes)
    }

    private suspend fun insertItems(recipes: List<Recipe>) = db.withTransaction {
        for (recipe in recipes) {
            insertItems(recipe)
        }
    }

    private suspend fun insertRecipesInternal(recipes: List<Recipe>) = db.withTransaction {
        for (recipe in recipes) {
            insertRecipes(recipe)
        }
    }

    private suspend fun insertItems(recipe: Recipe) {
        val bothItemList = recipe.getInputs() + recipe.getOutput()
        val bothPair = bothItemList.toItemAmountPair()

        // insert items
        val fullEntities = bothPair.map { it.first.toEntity() }
        db.getElementDao().insert(fullEntities)
    }

    private suspend fun insertRecipes(recipe: Recipe) {

        suspend fun Pair<Element, Int>.toId(): Long {
            return db.getElementDao().getId(first.unlocalizedName)
        }

        val inputPair = recipe.getInputs().toItemAmountPair()
        val outputPair = recipe.getOutput().toItemAmountPair()

        // insert recipes
        val recipeId = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
        val inputIdList = inputPair.map { it.toId() }
        val outputIdList = outputPair.map { it.toId() }

        when (recipe) {
            is GregtechRecipe -> {
                val recipeList = ArrayList<GregtechRecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    recipeList.add(GregtechRecipeEntity(
                        recipeId = recipeId,
                        targetItemId = inputIdList[index],
                        amount = pair.first.amount * pair.second,
                        machineId = recipe.machineId,
                        isEnabled = recipe.isEnabled,
                        duration = recipe.duration,
                        eut = recipe.eut
                    ))
                }
                db.getGregtechRecipeDao().insert(recipeList)
            }
            else -> {
                val recipeList = ArrayList<RecipeEntity>()
                for ((index, pair) in inputPair.withIndex()) {
                    recipeList.add(RecipeEntity(
                        recipeId = recipeId,
                        targetItemId = inputIdList[index],
                        amount = pair.second
                    ))
                }
                db.getRecipeDao().insert(recipeList)
            }
        }

        val resultList = ArrayList<RecipeResultEntity>()
        for ((index, pair) in outputPair.withIndex()) {
            resultList.add(RecipeResultEntity(
                recipeId = recipeId,
                resultItemId = outputIdList[index],
                amount = pair.first.amount * pair.second
            ))
        }
        db.getRecipeResultDao().insert(resultList)
    }

    private fun List<Element?>.toItemAmountPair(): List<Pair<Element, Int>> {
        return this.asSequence()
            .filter { it != null }
            .groupBy { it!!.unlocalizedName }
            .map { it.value[0]!! to it.value.size }
            .toList()
    }

    private fun Element.toEntity() = ElementEntity(
        unlocalizedName = unlocalizedName,
        localizedName = localizedName,
        type = if (this is Fluid) FLUID else ITEM
    )
}