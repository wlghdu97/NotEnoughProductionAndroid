package com.xhlab.nep.ui.element

import com.xhlab.multiplatform.util.Resource
import com.xhlab.multiplatform.util.Resource.Companion.isSuccessful
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.domain.item.LoadElementDetailUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class ElementDetailViewModel @Inject constructor(
    private val loadElementDetailUseCase: LoadElementDetailUseCase
) : ViewModel() {

    private val _element = MutableStateFlow<Resource<RecipeElement>?>(null)
    val element = _element.transform {
        if (it?.isSuccessful() == true) {
            emit(it.data!!)
        }
    }

    fun init(elementId: Long) {
        // ignore if element is already loaded
        if (_element.value != null) {
            return
        }
        // load element detail
        invokeUseCase(
            resultData = _element,
            useCase = loadElementDetailUseCase,
            params = elementId
        )
    }
}
