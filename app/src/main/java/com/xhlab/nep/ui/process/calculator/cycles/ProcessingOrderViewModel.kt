package com.xhlab.nep.ui.process.calculator.cycles

import androidx.lifecycle.ViewModel
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.domain.process.LoadProcessWithSubProcessListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessingOrderViewModel @Inject constructor(
    private val loadSubProcessListUseCase: LoadProcessWithSubProcessListUseCase
) : ViewModel(), BaseViewModel by BasicViewModel() {

    val subProcessList = loadSubProcessListUseCase.observeOnly(Resource.Status.SUCCESS)

    fun init(process: Process) {
        invokeMediatorUseCase(
            useCase = loadSubProcessListUseCase,
            params = LoadProcessWithSubProcessListUseCase.Parameter(process)
        )
    }
}