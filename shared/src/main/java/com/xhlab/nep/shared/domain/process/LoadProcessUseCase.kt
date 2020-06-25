package com.xhlab.nep.shared.domain.process

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.shared.util.Resource
import javax.inject.Inject

class LoadProcessUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : MediatorUseCase<LoadProcessUseCase.Parameter, ProcessView>() {

    override fun executeInternal(params: Parameter) = liveData<Resource<ProcessView>> {
        val liveData = processRepo.getProcess(params.processId)
        emitSource(Transformations.map(liveData) { Resource.success(it) })
    }

    data class Parameter(val processId: String)
}