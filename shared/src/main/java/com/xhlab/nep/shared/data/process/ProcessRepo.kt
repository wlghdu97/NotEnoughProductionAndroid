package com.xhlab.nep.shared.data.process

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.process.view.ProcessView

interface ProcessRepo {
    fun getProcess(processId: String): LiveData<ProcessView?>
    fun getProcesses(): DataSource.Factory<Int, ProcessView>
}