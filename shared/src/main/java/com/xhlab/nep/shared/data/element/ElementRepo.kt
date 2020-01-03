package com.xhlab.nep.shared.data.element

import androidx.paging.DataSource
import com.xhlab.nep.shared.domain.item.model.ElementView

interface ElementRepo {
    suspend fun deleteAll()
    fun searchByName(term: String): DataSource.Factory<Int, ElementView>
}