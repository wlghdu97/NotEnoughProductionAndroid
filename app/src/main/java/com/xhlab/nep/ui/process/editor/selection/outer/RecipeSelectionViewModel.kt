package com.xhlab.nep.ui.process.editor.selection.outer

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import javax.inject.Inject

class RecipeSelectionViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    MachineListener,
    RecipeSelectionListener
{
    private val processId = MutableLiveData<String>()

    private val _elements = MediatorLiveData<Resource<List<ElementView>>>()
    val elements = Transformations.map(_elements) {
        if (it.isSuccessful() && it?.data!!.size > 1) it.data else null
    }

    private val _element = MediatorLiveData<ElementView?>()
    val element: LiveData<ElementView?>
        get() = _element

    private val _constraint = MutableLiveData<ProcessEditViewModel.ConnectionConstraint>()
    val constraint: LiveData<ProcessEditViewModel.ConnectionConstraint>
        get() = _constraint

    private val _navigateToDetails = LiveEvent<Pair<Long, Int>>()
    val navigateToDetails: LiveData<Pair<Long, Int>>
        get() = _navigateToDetails

    private val _connectionResult = LiveEvent<Resource<Unit>>()
    val connectionResult: LiveData<Resource<Unit>>
        get() = _connectionResult

    init {
        _element.addSource(_elements) {
            val elements = it?.data
            if (elements?.size == 1) {
                _element.postValue(elements[0])
            }
        }
    }

    fun init(
        processId: String?,
        connectToParent: Boolean?,
        from: Recipe?,
        degree: Int?,
        elementKey: String?
    ) {
        when {
            processId == null ||
            connectToParent == null ||
            from == null ||
            degree == null ||
            elementKey == null ->
                throw NullPointerException("init values are null.")
            else -> {
                this.processId.value = processId
                invokeUseCase(
                    resultData = _elements,
                    useCase = loadElementDetailWithKeyUseCase,
                    params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
                )
                _constraint.postValue(
                    ProcessEditViewModel.ConnectionConstraint(
                        connectToParent, from, degree, elementKey
                    )
                )
            }
        }
    }

    fun submitElement(element: ElementView) {
        _element.postValue(element)
    }

    private fun requireElementId()
            = element.value?.id ?: throw NullPointerException("element id is null.")

    private fun requireProcessId()
            = processId.value ?: throw NullPointerException("process id is null.")

    override fun onClick(machineId: Int) {
        _navigateToDetails.postValue(requireElementId() to machineId)
    }

    override fun onSelect(from: Recipe, to: Recipe, element: Element, reversed: Boolean) {
        launchSuspendFunction(_connectionResult) {
            processRepo.connectRecipe(requireProcessId(), from, to, element, reversed)
        }
    }
}