package com.xhlab.nep.shared.data.recipe

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.multiplatform.paging.map
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.db.createOffsetLimitPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class RecipeRepoImpl constructor(
    private val db: Nep,
    private val recipeAdder: RecipeAdder
) : RecipeRepo {

    private val io = Dispatchers.IO

    override suspend fun getElementListByRecipe(recipeId: Long) = withContext(io) {
        db.recipeQueries.getElementsOfRecipe(recipeId, recipeElementViewMapper).executeAsList()
    }

    override suspend fun searchRecipeByElement(
        elementId: Long,
        term: String
    ): Pager<Int, RecipeView> {
        return with(db.recipeQueries) {
            val searchTerm = "*$term*"
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    if (term.isEmpty()) {
                        searchRecipeIdByElement(elementId, limit, offset)
                    } else {
                        searchRecipeIdByElementFts(elementId, searchTerm, limit, offset)
                    }
                },
                countQuery = if (term.isEmpty()) {
                    searchRecipeIdCountByElement(elementId)
                } else {
                    searchRecipeIdCountByElementFts(elementId, searchTerm)
                },
                transactor = this
            ).map {
                RecipeViewImpl(
                    recipeId = it,
                    itemList = getElementsOfRecipe(it, recipeElementViewMapper).executeAsList(),
                    resultItemList = db.recipeResultQueries.getElementsOfResult(
                        recipeId = it,
                        mapper = recipeElementViewWithMetaDataMapper
                    ).executeAsList()
                )
            }
        }
    }

    override suspend fun searchUsageRecipeByElement(
        elementId: Long,
        term: String
    ): Pager<Int, RecipeView> {
        return with(db.recipeQueries) {
            val searchTerm = "*$term*"
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    if (term.isEmpty()) {
                        searchUsageRecipeIdByElement(elementId, limit, offset)
                    } else {
                        searchUsageRecipeIdByElementFts(elementId, searchTerm, limit, offset)
                    }
                },
                countQuery = if (term.isEmpty()) {
                    searchUsageRecipeIdCountByElement(elementId)
                } else {
                    searchUsageRecipeIdCountByElementFts(elementId, searchTerm)
                },
                transactor = this
            ).map {
                RecipeViewImpl(
                    recipeId = it,
                    itemList = db.recipeResultQueries.getElementsOfResult(
                        recipeId = it,
                        mapper = recipeElementViewWithMetaDataMapper
                    ).executeAsList(),
                    resultItemList = getElementsOfRecipe(
                        recipeId = it,
                        mapper = recipeElementViewMapper
                    ).executeAsList()
                )
            }
        }
    }

    override suspend fun insertRecipes(recipes: List<Recipe>) = withContext(io) {
        recipeAdder.insertRecipes(recipes)
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

    private val recipeElementViewMapper = { id: Long,
                                            localized_name: String,
                                            unlocalized_name: String,
                                            type: Int,
                                            amount: Int ->
        RecipeElementViewImpl(
            id = id,
            localizedName = localized_name,
            unlocalizedName = unlocalized_name,
            type = type,
            metaData = null,
            amount = amount
        )
    }

    private val recipeElementViewWithMetaDataMapper = { id: Long,
                                                        localized_name: String,
                                                        unlocalized_name: String,
                                                        type: Int,
                                                        amount: Int,
                                                        meta_data: String? ->
        RecipeElementViewImpl(
            id = id,
            localizedName = localized_name,
            unlocalizedName = unlocalized_name,
            type = type,
            metaData = meta_data,
            amount = amount
        )
    }

    data class RecipeViewImpl(
        override val recipeId: Long,
        override val itemList: List<RecipeElementView>,
        override val resultItemList: List<RecipeElementView>
    ) : RecipeView()

    companion object {
        private const val PAGE_SIZE = 10
    }
}
