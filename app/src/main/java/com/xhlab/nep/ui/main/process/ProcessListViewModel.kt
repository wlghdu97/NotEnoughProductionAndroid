package com.xhlab.nep.ui.main.process

import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.process.LoadProcessListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessListViewModel @Inject constructor(
    loadProcessListUseCase: LoadProcessListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ProcessListener
{
    val processList = loadProcessListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    init {
        invokeMediatorUseCase(
            useCase = loadProcessListUseCase,
            params = Unit
        )
    }

    override fun onClick(id: String) {

    }
}