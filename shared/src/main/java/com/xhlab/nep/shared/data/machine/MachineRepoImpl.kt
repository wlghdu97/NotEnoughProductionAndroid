package com.xhlab.nep.shared.data.machine

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.MachineEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MachineRepoImpl @Inject constructor(
    private val db: AppDatabase
) : MachineRepo {

    private val io = Dispatchers.IO

    override suspend fun getMachine(machineId: Int) = withContext(io) {
        db.getMachineDao().getMachine(machineId)?.toMachine()
    }

    override suspend fun insertMachine(modName: String, machineName: String) = withContext(io) {
        db.getMachineDao().insert(MachineEntity(modName = modName, name = machineName))
        db.getMachineDao().getId(modName, machineName)
    }

    override suspend fun deleteAll() = withContext(io) {
        db.getMachineDao().deleteAll()
    }

    override fun getMachines() = db.getMachineDao().getMachines().map { it.toMachine() }
}