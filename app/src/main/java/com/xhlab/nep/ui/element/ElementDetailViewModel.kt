package com.xhlab.nep.ui.element

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.shared.domain.item.LoadElementDetailUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.shared.util.isSuccessful
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ElementDetailViewModel @Inject constructor(
    private val loadElementDetailUseCase: LoadElementDetailUseCase
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val _element = MediatorLiveData<Resource<ElementView>>()
    val element = Transformations.map(_element) {
        if (it.isSuccessful()) it.data else null
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
