package com.xhlab.nep.ui.main.machines.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.model.Machine
import com.xhlab.nep.shared.domain.machine.LoadMachineResultListUseCase
import com.xhlab.nep.shared.domain.machine.LoadMachineUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class MachineResultViewModel @Inject constructor(
    private val loadMachineUseCase: LoadMachineUseCase,
    private val loadMachineResultListUseCase: LoadMachineResultListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase
) : ViewModel(), BaseViewModel by BasicViewModel(), ElementListener {

    private val _machine = MutableLiveData<Resource<Machine?>>()
    val machine = Transformations.map(_machine) {
        if (it.isSuccessful()) it.data else null
    }

    val resultList = loadMachineResultListUseCase.observeOnly(Resource.Status.SUCCESS)

    fun init(machineId: Int?) {
        if (machineId == null || machineId == -1) {
            throw NullPointerException("machine id is null.")
        }
        invokeUseCase(
            useCase = loadMachineUseCase,
            params = LoadMachineUseCase.Parameter(machineId),
            resultData = _machine
        )
        invokeMediatorUseCase(
            useCase = loadMachineResultListUseCase,
            params = LoadMachineResultListUseCase.Parameter(machineId)
        )
    }

    override fun onClick(elementId: Long, elementType: Int) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }
}