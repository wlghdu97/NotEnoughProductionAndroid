package com.xhlab.nep.shared.domain.process

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadProcessWithSubProcessListUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : MediatorUseCase<LoadProcessWithSubProcessListUseCase.Parameter, List<Process>>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<List<Process>>> {
        val subProcessIds = params.rootProcess.getSubProcessIds()
        val subProcessList = processRepo.getSubProcesses(subProcessIds)
        emitSource(Transformations.map(subProcessList) { Resource.success(it) })
    }

    data class Parameter(val rootProcess: Process)
}