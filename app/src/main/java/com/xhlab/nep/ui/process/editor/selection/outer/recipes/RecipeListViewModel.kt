package com.xhlab.nep.ui.process.editor.selection.outer.recipes

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.domain.item.LoadElementDetailUseCase
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.domain.recipe.LoadRecipeMachineListUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageMachineListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.machines.MachineListener
import javax.inject.Inject

class RecipeListViewModel @Inject constructor(
    private val loadElementDetailUseCase: LoadElementDetailUseCase,
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase,
    private val loadRecipeMachineListUseCase: LoadRecipeMachineListUseCase,
    private val loadUsageMachineListUseCase: LoadUsageMachineListUseCase
) : ViewModel(), BaseViewModel by BasicViewModel(), MachineListener {

    private val connectToParent = MutableLiveData<Boolean>()

    private val _elements = MediatorLiveData<Resource<List<ElementView>>>()
    val elements = Transformations.map(_elements) {
        if (it.isSuccessful() && it?.data!!.size > 1) it.data else null
    }

    private val _element = MediatorLiveData<Resource<ElementView>>()
    val element = Transformations.map(_element) {
        if (it.isSuccessful()) it.data else null
    }

    val recipeList = loadRecipeMachineListUseCase.observeOnly(Resource.Status.SUCCESS)
    val usageList = loadUsageMachineListUseCase.observeOnly(Resource.Status.SUCCESS)

    private val _navigateToDetails = LiveEvent<Triple<Long, Int, Boolean>>()
    val navigateToDetails: LiveData<Triple<Long, Int, Boolean>>
        get() = _navigateToDetails

    init {
        _element.addSource(_elements) {
            val elements = it?.data
            if (elements?.size == 1) {
                _element.postValue(Resource.success(elements[0]))
            }
        }
        _element.addSource(element) {
            if (it != null) {
                when (connectToParent.value == true) {
                    true -> {
                        invokeMediatorUseCase(
                            useCase = loadUsageMachineListUseCase,
                            params = LoadUsageMachineListUseCase.Parameter(it.id)
                        )
                    }
                    false -> {
                        invokeMediatorUseCase(
                            useCase = loadRecipeMachineListUseCase,
                            params = LoadRecipeMachineListUseCase.Parameter(it.id)
                        )
                    }
                }
            }
        }
    }

    fun init(elementId: Long?, connectToParent: Boolean?) {
        requireNotNull(elementId)
        requireNotNull(connectToParent)
        this.connectToParent.value = connectToParent
        invokeUseCase(
            resultData = _element,
            useCase = loadElementDetailUseCase,
            params = elementId
        )
    }

    fun init(elementKey: String?, connectToParent: Boolean?) {
        requireNotNull(elementKey)
        requireNotNull(connectToParent)
        this.connectToParent.value = connectToParent
        invokeUseCase(
            resultData = _elements,
            useCase = loadElementDetailWithKeyUseCase,
            params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
        )
    }

    private fun requireElementId()
            = element.value?.id ?: throw NullPointerException("element id is null.")

    fun submitElement(element: ElementView) {
        _element.postValue(Resource.success(element))
    }

    override fun onClick(machineId: Int) {
        val connectToParent = connectToParent.value == true
        _navigateToDetails.postValue(Triple(requireElementId(), machineId, connectToParent))
    }
}