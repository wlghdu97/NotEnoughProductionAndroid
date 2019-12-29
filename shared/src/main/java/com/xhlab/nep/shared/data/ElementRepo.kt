package com.xhlab.nep.shared.data

import com.xhlab.nep.shared.db.AppDatabase
import javax.inject.Inject

internal class ElementRepo @Inject constructor(private val db: AppDatabase) {
    fun searchByName(term: String) = db.getElementDao().searchByName(term)
}