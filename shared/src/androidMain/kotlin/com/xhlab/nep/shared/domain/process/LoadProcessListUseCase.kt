package com.xhlab.nep.shared.domain.process

import com.xhlab.multiplatform.paging.Pager
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class LoadProcessListUseCase @Inject constructor(
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
