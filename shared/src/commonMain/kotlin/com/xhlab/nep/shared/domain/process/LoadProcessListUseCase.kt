package com.xhlab.nep.shared.domain.process

import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("ProcessDomain")
class LoadProcessListUseCase constructor(
    private val processRepo: ProcessRepo
) : BaseMediatorUseCase<LoadProcessListUseCase.Parameter, Pager<Int, ProcessSummary>>() {

    override suspend fun executeInternal(params: Parameter): Flow<Resource<Pager<Int, ProcessSummary>>> {
        val pager = when (params.targetElementKey.isNullOrEmpty()) {
            true -> processRepo.getProcesses()
            false -> processRepo.getProcessesByTarget(params.targetElementKey)
        }
        return flowOf(Resource.success(pager))
    }

    data class Parameter(val targetElementKey: String? = null)
}
