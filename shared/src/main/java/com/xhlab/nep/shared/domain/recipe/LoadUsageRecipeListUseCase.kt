package com.xhlab.nep.shared.domain.recipe

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadUsageRecipeListUseCase @Inject constructor(
    private val recipeRepo: RecipeRepo,
    private val machineRecipeRepo: MachineRecipeRepo
) : MediatorUseCase<LoadUsageRecipeListUseCase.Parameters, PagedList<RecipeView>>() {

    override fun executeInternal(params: Parameters) = liveData<Resource<PagedList<RecipeView>>> {
        val (elementId, machineId, term) = params
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = when (machineId == CRAFTING_BENCH) {
            true -> recipeRepo.searchUsageRecipeByElement(elementId, term)
            false -> machineRecipeRepo.searchUsageRecipeByElement(elementId, machineId, term)
        }
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameters(
        val elementId: Long,
        val machineId: Int,
        val term: String = ""
    )

    companion object {
        const val CRAFTING_BENCH = -1
        private const val PAGE_SIZE = 10
    }
}
