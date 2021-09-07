package com.xhlab.nep.shared.domain.process

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadProcessUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : MediatorUseCase<LoadProcessUseCase.Parameter, Process>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<Process>> {
        val liveData = processRepo.getProcessLiveData(params.processId)
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val processId: String)
}
