package com.xhlab.nep.shared.data.element

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.recipes.view.RecipeMachineView

interface ElementRepo {
    suspend fun getIdsByKey(unlocalizedName: String): List<Long>
    suspend fun getElementDetail(id: Long): ElementView?
    suspend fun getReplacementCountByElement(oreDictName: String): Int
    suspend fun deleteAll()
    fun searchByName(term: String): Pager<Int, ElementView>
    fun searchMachineResults(machineId: Int, term: String): Pager<Int, ElementView>
    fun searchMachineResultsFts(machineId: Int, term: String): Pager<Int, ElementView>
    fun getElements(): Pager<Int, ElementView>
    fun getResultsByMachine(machineId: Int): Pager<Int, ElementView>
    fun getRecipeMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView>
    fun getUsageMachinesByElement(elementId: Long): Pager<Int, RecipeMachineView>
    fun getUsagesByElement(elementId: Long): Pager<Int, ElementView>
    fun getOreDictsByElement(elementId: Long): Pager<Int, String>
    fun getReplacementsByElement(oreDictName: String): Pager<Int, ElementView>
}
