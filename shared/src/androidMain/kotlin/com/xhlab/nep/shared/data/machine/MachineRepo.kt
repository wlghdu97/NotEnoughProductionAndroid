package com.xhlab.nep.shared.data.machine

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.nep.model.Machine

interface MachineRepo {
    suspend fun getMachine(machineId: Int): Machine?
    suspend fun insertMachine(modName: String, machineName: String): Int?
    suspend fun deleteAll()
    fun getMachines(): Pager<Int, Machine>
}
