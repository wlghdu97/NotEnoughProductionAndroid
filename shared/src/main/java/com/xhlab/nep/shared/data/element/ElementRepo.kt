package com.xhlab.nep.shared.data.element

import androidx.paging.DataSource
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.recipes.view.RecipeMachineView

interface ElementRepo {
    suspend fun getIdByKey(unlocalizedName: String): Long?
    suspend fun getElementDetail(id: Long): ElementView?
    suspend fun deleteAll()
    fun searchByName(term: String): DataSource.Factory<Int, ElementView>
    fun searchMachineResults(machineId: Int, term: String): DataSource.Factory<Int, ElementView>
    fun searchMachineResultsFts(machineId: Int, term: String): DataSource.Factory<Int, ElementView>
    fun getElements(): DataSource.Factory<Int, ElementView>
    fun getResultsByMachine(machineId: Int): DataSource.Factory<Int, ElementView>
    fun getMachinesByElement(elementId: Long): DataSource.Factory<Int, RecipeMachineView>
    fun getUsagesByElement(elementId: Long): DataSource.Factory<Int, ElementView>
    fun getOreDictsByElement(elementId: Long): DataSource.Factory<Int, String>
    fun getReplacementsByElement(oreDictName: String): DataSource.Factory<Int, ElementView>
}