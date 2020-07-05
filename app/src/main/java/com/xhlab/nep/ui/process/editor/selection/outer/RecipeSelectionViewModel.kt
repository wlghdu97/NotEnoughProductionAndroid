package com.xhlab.nep.ui.process.editor.selection.outer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.machines.MachineListener
import javax.inject.Inject

class RecipeSelectionViewModel @Inject constructor(
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase
) : ViewModel(), BaseViewModel by BasicViewModel(), MachineListener {

    private val _element = MediatorLiveData<Resource<ElementView>>()
    val element = Transformations.map(_element) {
        if (it.isSuccessful()) it.data else null
    }

    private val _navigateToDetails = LiveEvent<Pair<Long, Int>>()
    val navigateToDetails: LiveData<Pair<Long, Int>>
        get() = _navigateToDetails

    fun init(elementKey: String?) {
        if (elementKey == null) {
            throw NullPointerException("element key is null.")
        }
        invokeUseCase(
            resultData = _element,
            useCase = loadElementDetailWithKeyUseCase,
            params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
        )
    }

    private fun requireElementId()
            = element.value?.id ?: throw NullPointerException("element id is null.")

    override fun onClick(machineId: Int) {
        _navigateToDetails.postValue(requireElementId() to machineId)
    }
}