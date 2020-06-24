package com.xhlab.nep.shared.data.element

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.model.ElementView
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

    override fun searchMachineResults(machineId: Int, term: String)
            = db.getElementDao().searchMachineResults(machineId, term).map { it as ElementView }

    override fun searchMachineResultsFts(machineId: Int, term: String)
            = db.getElementDao().searchMachineResultsFts(machineId, term).map { it as ElementView }

    override fun getElements()
            = db.getElementDao().getElements().map { it as ElementView }

    override fun getResultsByMachine(machineId: Int)
            = db.getElementDao().getMachineResults(machineId).map { it as ElementView }

    override fun getMachinesByElement(elementId: Long)
            = db.getElementDao().getMachinesByElement(elementId)

    override fun getUsagesByElement(elementId: Long)
            = db.getElementDao().getUsagesByElement(elementId).map { it as ElementView }

    override fun getOreDictsByElement(elementId: Long)
            = db.getElementDao().getOreDictsByElement(elementId)

    override fun getReplacementsByElement(oreDictName: String)
            = db.getElementDao().getReplacementList(oreDictName).map { it as ElementView }
}