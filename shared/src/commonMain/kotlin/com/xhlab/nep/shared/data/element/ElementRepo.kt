package com.xhlab.nep.shared.data.element

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.recipes.view.RecipeMachineView
import kr.sparkweb.multiplatform.paging.Pager

interface ElementRepo {
    suspend fun getIdsByKey(unlocalizedName: String): List<Long>
    suspend fun getElementDetail(id: Long): RecipeElement?
    suspend fun getReplacementCountByElement(oreDictName: String): Int
    suspend fun deleteAll()
    fun searchByName(term: String): Pager<Int, RecipeElement>
    fun searchMachineResults(machineId: Int, term: String): Pager<Int, RecipeElement>
    fun searchMachineResultsFts(machineId: Int, term: String): Pager<Int, RecipeElement>
    fun getElements(): Pager<Int, RecipeElement>
    fun getResultsByMachine(machineId: Int): Pager<Int, RecipeElement>
    fun getRecipeMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView>
    fun getUsageMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView>
    fun getUsagesByElement(elementId: Long): Pager<Int, RecipeElement>
    fun getOreDictsByElement(elementId: Long): Pager<Int, String>
    fun getReplacementsByElement(oreDictName: String): Pager<Int, RecipeElement>
}
