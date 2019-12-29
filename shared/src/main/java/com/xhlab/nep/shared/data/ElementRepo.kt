package com.xhlab.nep.shared.data

import com.xhlab.nep.shared.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ElementRepo @Inject constructor(private val db: AppDatabase) {

    private val io = Dispatchers.IO

    fun searchByName(term: String) = db.getElementDao().searchByName(term)

    suspend fun deleteAll() = withContext(io) {
        db.getElementDao().deleteAll()
    }
}