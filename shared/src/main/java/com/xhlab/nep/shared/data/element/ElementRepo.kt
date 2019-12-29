package com.xhlab.nep.shared.data.element

import androidx.paging.DataSource
import com.xhlab.nep.shared.db.view.SearchResultView

interface ElementRepo {
    fun searchByName(term: String): DataSource.Factory<Int, SearchResultView>
    suspend fun deleteAll()
}