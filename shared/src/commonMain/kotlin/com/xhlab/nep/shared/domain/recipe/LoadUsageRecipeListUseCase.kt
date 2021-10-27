package com.xhlab.nep.shared.domain.recipe

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@ProvideWithDagger("RecipeDomain")
class LoadUsageRecipeListUseCase constructor(
    private val recipeRepo: RecipeRepo,
    private val machineRecipeRepo: MachineRecipeRepo
) : BaseMediatorUseCase<LoadUsageRecipeListUseCase.Parameters, Pager<Int, RecipeView>>() {

    override suspend fun executeInternal(params: Parameters): Flow<Resource<Pager<Int, RecipeView>>> {
        val (elementId, machineId, term) = params
        val pager = when (machineId == CRAFTING_BENCH) {
            true -> recipeRepo.searchUsageRecipeByElement(elementId, term)
            false -> machineRecipeRepo.searchUsageRecipeByElement(elementId, machineId, term)
        }
        return flowOf(Resource.success(pager))
    }

    data class Parameters(
        val elementId: Long,
        val machineId: Int,
        val term: String = ""
    )

    companion object {
        const val CRAFTING_BENCH = -1
    }
}
