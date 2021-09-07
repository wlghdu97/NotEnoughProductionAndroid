package com.xhlab.nep.ui.process.calculator

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.process.calculator.ingredients.ElementKeyListener
import javax.inject.Inject

class ElementNavigatorViewModel @Inject constructor(
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase
) : ViewModel(), BaseViewModel by BasicViewModel(),
    ElementKeyListener {

    private val _elements = MediatorLiveData<Resource<List<ElementView>>>()
    val elements = Transformations.map(_elements) {
        if (it.isSuccessful()) {
            if (it.data!!.size == 1) {
                val element = it.data!![0]
                navigateToDetails(element.id, element.type)
                null
            } else {
                it.data
            }
        } else {
            null
        }
    }

    fun submitElement(element: ElementView) {
        navigateToDetails(element.id, element.type)
    }

    override fun onClick(elementKey: String) {
        invokeUseCase(
            resultData = _elements,
            useCase = loadElementDetailWithKeyUseCase,
            params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
        )
    }

    private fun navigateToDetails(elementId: Long, elementType: Int) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }
}
