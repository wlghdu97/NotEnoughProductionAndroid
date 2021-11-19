package com.xhlab.nep.shared.data.machinerecipe

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.paging.PagingConfig
import com.xhlab.nep.model.PlainRecipeElement
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.pagerScope
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.db.createOffsetLimitPager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class MachineRecipeRepoImpl constructor(
    private val db: Nep,
    private val io: CoroutineDispatcher
) : MachineRecipeRepo {

    override suspend fun getElementListByRecipe(recipeId: Long) = withContext(io) {
        db.machineRecipeQueries.getElementListOfRecipe(recipeId, recipeElementViewMapper)
            .executeAsList()
    }

    override suspend fun searchRecipeByElement(
        elementId: Long,
        machineId: Int,
        term: String
    ): Pager<Int, RecipeView> {
        return with(db.machineRecipeQueries) {
            val searchTerm = "*$term*"
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    if (term.isEmpty()) {
                        searchRecipeIdByElement(
                            elementId = elementId,
                            machineId = machineId,
                            limit = limit,
                            offset = offset,
                            mapper = machineRecipeViewMapper
                        )
                    } else {
                        searchRecipeIdByElementFts(
                            elementId = elementId,
                            machineId = machineId,
                            term = searchTerm,
                            limit = limit,
                            offset = offset,
                            mapper = machineRecipeViewMapper
                        )
                    }
                },
                countQuery = if (term.isEmpty()) {
                    searchRecipeIdCountByElement(elementId, machineId)
                } else {
                    searchRecipeIdCountByElementFts(elementId, machineId, searchTerm)
                },
                transactor = this
            ).map {
                it.copy(
                    itemList = getElementListOfRecipe(
                        recipeId = it.recipeId,
                        mapper = recipeElementViewMapper
                    ).executeAsList(),
                    resultItemList = db.recipeResultQueries.getElementsOfResult(
                        recipeId = it.recipeId,
                        mapper = recipeElementViewMapper
                    ).executeAsList()
                )
            }
        }
    }

    override suspend fun searchUsageRecipeByElement(
        elementId: Long,
        machineId: Int,
        term: String
    ): Pager<Int, RecipeView> {
        return with(db.machineRecipeQueries) {
            val searchTerm = "*$term*"
            createOffsetLimitPager(
                clientScope = pagerScope,
                ioDispatcher = io,
                config = pagingConfig,
                queryProvider = { limit, offset ->
                    if (term.isEmpty()) {
                        searchUsageRecipeIdByElement(
                            machineId = machineId,
                            elementId = elementId,
                            limit = limit,
                            offset = offset,
                            mapper = machineRecipeViewMapper
                        )
                    } else {
                        searchUsageRecipeIdByElementFts(
                            machineId = machineId,
                            elementId = elementId,
                            term = searchTerm,
                            limit = limit,
                            offset = offset,
                            mapper = machineRecipeViewMapper
                        )
                    }
                },
                countQuery = if (term.isEmpty()) {
                    searchUsageRecipeIdCountByElement(machineId, elementId)
                } else {
                    searchUsageRecipeIdCountByElementFts(machineId, elementId, searchTerm)
                },
                transactor = this
            ).map {
                it.copy(
                    itemList = db.recipeResultQueries.getElementsOfResult(
                        recipeId = it.recipeId,
                        mapper = recipeElementViewMapper
                    ).executeAsList(),
                    resultItemList = getElementListOfRecipe(
                        recipeId = it.recipeId,
                        mapper = recipeElementViewMapper
                    ).executeAsList()
                )
            }
        }
    }

    private val pagingConfig: PagingConfig
        get() = PagingConfig(PAGE_SIZE)

    private val recipeElementViewMapper = { id: Long,
                                            localized_name: String,
                                            unlocalized_name: String,
                                            type: Int,
                                            amount: Int,
                                            meta_data: String? ->
        PlainRecipeElement(
            id = id,
            localizedName = localized_name,
            unlocalizedName = unlocalized_name,
            type = type,
            metaData = meta_data,
            amount = amount
        )
    }

    private val machineRecipeViewMapper = { recipe_id: Long,
                                            enabled: Boolean,
                                            duration: Int,
                                            power_type: Int,
                                            ept: Int,
                                            _: String?,
                                            machine_id: Int,
                                            machine_name: String ->
        MachineRecipeViewImpl(
            recipeId = recipe_id,
            itemList = emptyList(),
            resultItemList = emptyList(),
            isEnabled = enabled,
            duration = duration,
            powerType = power_type,
            ept = ept,
            machineId = machine_id,
            machineName = machine_name
        )
    }

    @Serializable
    data class MachineRecipeViewImpl(
        override val recipeId: Long,
        override val itemList: List<RecipeElement>,
        override val resultItemList: List<RecipeElement>,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String
    ) : MachineRecipeView()

    companion object {
        private const val PAGE_SIZE = 10
    }
}
