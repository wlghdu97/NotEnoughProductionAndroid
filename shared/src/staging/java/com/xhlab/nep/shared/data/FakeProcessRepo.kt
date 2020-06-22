package com.xhlab.nep.shared.data

import androidx.paging.DataSource
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.ListDataSource
import com.xhlab.test_shared.ProcessData
import javax.inject.Inject

class FakeProcessRepo @Inject constructor() : ProcessRepo {

    override fun getProcesses(): DataSource.Factory<Int, Process> {
        return ListDataSource(listOf(ProcessData.processGlass, ProcessData.processPE))
    }
}