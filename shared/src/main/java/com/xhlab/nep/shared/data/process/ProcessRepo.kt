package com.xhlab.nep.shared.data.process

import androidx.paging.DataSource
import com.xhlab.nep.model.process.Process

interface ProcessRepo {
    fun getProcesses(): DataSource.Factory<Int, Process>
}