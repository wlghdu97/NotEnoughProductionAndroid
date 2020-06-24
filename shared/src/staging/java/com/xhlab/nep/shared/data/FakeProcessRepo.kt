package com.xhlab.nep.shared.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.ListDataSource
import com.xhlab.test_shared.ProcessViewData
import javax.inject.Inject

class FakeProcessRepo @Inject constructor() : ProcessRepo {

    override fun getProcess(processId: String): LiveData<ProcessView?> {
        return MutableLiveData(ProcessViewData.processViewList.find { it.id == processId })
    }

    override fun getProcesses(): DataSource.Factory<Int, ProcessView> {
        return ListDataSource(ProcessViewData.processViewList)
    }
}