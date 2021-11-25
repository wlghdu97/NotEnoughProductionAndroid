package com.xhlab.nep.shared.ui.process.calculator

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.domain.item.LoadElementDetailWithKeyUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.shared.ui.process.calculator.ingredients.ElementKeyListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessViewModel")
class ElementNavigatorViewModel constructor(
    private val loadElementDetailWithKeyUseCase: LoadElementDetailWithKeyUseCase
) : ViewModel(), ElementKeyListener {

    private val _elements = MutableStateFlow<Resource<List<RecipeElement>>?>(null)
    val elements = _elements.transform {
        if (it?.isSuccessful() == true) {
            val data = it.data!!
            if (data.size == 1) {
                val element = data[0]
                navigateToDetails(element.id)
            } else {
                emit(data)
            }
        }
    }

    private val _navigateToElementDetail = EventFlow<Long>()
    val navigateElementToDetail: Flow<Long>
        get() = _navigateToElementDetail.flow

    fun submitElement(element: RecipeElement) {
        navigateToDetails(element.id)
    }

    override fun onClick(elementKey: String) {
        invokeUseCase(
            resultData = _elements,
            useCase = loadElementDetailWithKeyUseCase,
            params = LoadElementDetailWithKeyUseCase.Parameter(elementKey)
        )
    }

    private fun navigateToDetails(elementId: Long) {
        scope.launch {
            _navigateToElementDetail.emit(elementId)
        }
    }
}
