package com.xhlab.nep.shared.data

import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.entity.GregtechMachineEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class GregtechRepo @Inject constructor(private val db: AppDatabase) {

    private val io = Dispatchers.IO

    suspend fun insertGregtechMachine(machineName: String) = withContext(io) {
        db.getGregtechMachineDao().insert(GregtechMachineEntity(name = machineName))
        db.getGregtechMachineDao().getId(machineName)
    }

    suspend fun deleteGregtechMachines() = withContext(io) {
        db.getGregtechMachineDao().deleteAll()
    }
}