package com.xhlab.nep.shared.domain.process

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("ProcessDomain")
class LoadProcessUseCase constructor(
    private val processRepo: ProcessRepo
) : BaseMediatorUseCase<LoadProcessUseCase.Parameter, Process>() {

    override suspend fun executeInternal(params: Parameter): Flow<Resource<Process>> {
        val processFlow = processRepo.getProcessFlow(params.processId)
        return processFlow.map { Resource.success(it) }
    }

    data class Parameter(val processId: String)
}
