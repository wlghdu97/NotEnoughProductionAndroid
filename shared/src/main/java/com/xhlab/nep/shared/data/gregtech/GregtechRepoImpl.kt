package com.xhlab.nep.shared.data.gregtech

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.GregtechMachineEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GregtechRepoImpl @Inject constructor(
    private val db: AppDatabase
) : GregtechRepo {

    private val io = Dispatchers.IO

    override suspend fun insertGregtechMachine(machineName: String) = withContext(io) {
        db.getGregtechMachineDao().insert(GregtechMachineEntity(name = machineName))
        db.getGregtechMachineDao().getId(machineName)
    }

    override suspend fun deleteGregtechMachines() = withContext(io) {
        db.getGregtechMachineDao().deleteAll()
    }
}