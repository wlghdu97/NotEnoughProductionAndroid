package com.xhlab.nep.shared.data.machine

import androidx.paging.DataSource
import com.xhlab.nep.model.Machine

interface MachineRepo {
    suspend fun getId(machineName: String): Int?
    suspend fun deleteAll()
    fun getMachines(): DataSource.Factory<Int, Machine>
}