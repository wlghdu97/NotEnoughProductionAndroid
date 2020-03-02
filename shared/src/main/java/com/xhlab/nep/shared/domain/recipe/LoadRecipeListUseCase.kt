package com.xhlab.nep.shared.domain.recipe

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.recipe.model.RecipeView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadRecipeListUseCase @Inject constructor(
    private val recipeRepo: RecipeRepo,
    private val gregtechRepo: GregtechRepo
) : MediatorUseCase<LoadRecipeListUseCase.Parameters, PagedList<RecipeView>>() {

    override fun executeInternal(params: Parameters) = liveData<Resource<PagedList<RecipeView>>> {
        val (elementId, machineId) = params
        val config = PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
        val dataSource = when (machineId == CRAFTING_BENCH) {
            true -> recipeRepo.searchRecipeByElement(elementId)
            false -> gregtechRepo.searchRecipeByElement(elementId, machineId)
        }
        val liveData = LivePagedListBuilder(dataSource, config).build()
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameters(
        val elementId: Long,
        val machineId: Int
    )

    companion object {
        const val CRAFTING_BENCH = -1
        private const val PAGE_SIZE = 10
    }
}