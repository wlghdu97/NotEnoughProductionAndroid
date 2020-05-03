package com.xhlab.nep.shared.data.element

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.domain.item.model.ElementView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ElementRepoImpl @Inject constructor(
    private val db: AppDatabase
) : ElementRepo {

    private val io = Dispatchers.IO

    override suspend fun getElementDetail(id: Long) = withContext(io) {
        db.getElementDao().getElementDetail(id)
    }

    override suspend fun deleteAll() = withContext(io) {
        db.getElementDao().deleteAll()
    }

    override fun searchByName(term: String)
            = db.getElementDao().searchByName(term).map { it as ElementView }

    override fun searchGregtechResults(machineId: Int, term: String)
            = db.getElementDao().searchGregtechResults(machineId, term).map { it as ElementView }

    override fun searchGregtechResultsFts(machineId: Int, term: String)
            = db.getElementDao().searchGregtechResultsFts(machineId, term).map { it as ElementView }

    override fun getResultsByStation(machineId: Int)
            = db.getElementDao().getGregtechResults(machineId).map { it as ElementView }

    override fun getStationsByElement(elementId: Long)
            = db.getElementDao().getStationsByElement(elementId)

    override fun getUsagesByElement(elementId: Long)
            = db.getElementDao().getUsagesByElement(elementId).map { it as ElementView }

    override fun getOreDictsByElement(elementId: Long)
            = db.getElementDao().getOreDictsByElement(elementId)

    override fun getReplacementsByElement(oreDictName: String)
            = db.getElementDao().getReplacementList(oreDictName).map { it as ElementView }
}