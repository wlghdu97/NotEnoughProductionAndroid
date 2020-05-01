package com.xhlab.nep.shared.data.machine

import com.xhlab.nep.shared.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MachineRepoImpl @Inject constructor(
    private val db: AppDatabase
) : MachineRepo {

    private val io = Dispatchers.IO

    override suspend fun getId(machineName: String) = withContext(io) {
        db.getMachineDao().getId(machineName)
    }

    override suspend fun deleteAll() = withContext(io) {
        db.getMachineDao().deleteAll()
    }

    override fun getMachines() = db.getMachineDao().getMachines().map { it.toMachine() }
}