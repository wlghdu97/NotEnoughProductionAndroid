package com.xhlab.nep.shared.data.element

import com.xhlab.nep.shared.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ElementRepoImpl @Inject constructor(
    private val db: AppDatabase
) : ElementRepo {

    private val io = Dispatchers.IO

    override fun searchByName(term: String) = db.getElementDao().searchByName(term)

    override suspend fun deleteAll() = withContext(io) {
        db.getElementDao().deleteAll()
    }
}