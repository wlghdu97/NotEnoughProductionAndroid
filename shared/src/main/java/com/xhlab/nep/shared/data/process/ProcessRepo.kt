package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.process.Process

interface ProcessRepo {
    fun getProcess(processId: String): LiveData<Process?>
    fun getProcesses(): DataSource.Factory<Int, Process>
}