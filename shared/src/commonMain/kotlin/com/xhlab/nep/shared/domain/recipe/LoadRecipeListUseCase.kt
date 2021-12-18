package com.xhlab.nep.shared.domain.recipe

import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("RecipeDomain")
class LoadRecipeListUseCase constructor(
    private val recipeRepo: RecipeRepo,
    private val machineRecipeRepo: MachineRecipeRepo
) : BaseMediatorUseCase<LoadRecipeListUseCase.Parameters, Pager<Int, RecipeView>>() {

    override suspend fun executeInternal(params: Parameters): Flow<Resource<Pager<Int, RecipeView>>> {
        val (elementId, machineId, term) = params
        val pager = when (machineId == CRAFTING_BENCH) {
            true -> recipeRepo.searchRecipeByElement(elementId, term)
            false -> machineRecipeRepo.searchRecipeByElement(elementId, machineId, term)
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
