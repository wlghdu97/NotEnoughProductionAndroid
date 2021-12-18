package com.xhlab.nep.shared.ui.element

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.domain.item.LoadElementDetailUseCase
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.transform
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.Resource
import kr.sparkweb.multiplatform.util.Resource.Companion.isSuccessful

@ProvideWithDagger("ViewModel")
class ElementDetailViewModel constructor(
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
