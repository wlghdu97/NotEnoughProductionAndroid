package com.xhlab.nep.shared.data.element

import androidx.paging.DataSource
import com.xhlab.nep.shared.domain.item.model.ElementView
import com.xhlab.nep.shared.domain.recipe.model.StationView

interface ElementRepo {
    suspend fun getElementDetail(id: Long): ElementView?
    suspend fun deleteAll()
    fun searchByName(term: String): DataSource.Factory<Int, ElementView>
    fun getStationsByElement(elementId: Long): DataSource.Factory<Int, StationView>
    fun getUsagesByElement(elementId: Long): DataSource.Factory<Int, ElementView>
    fun getOreDictsByElement(elementId: Long): DataSource.Factory<Int, String>
    fun getReplacementsByElement(oreDictName: String): DataSource.Factory<Int, ElementView>
}