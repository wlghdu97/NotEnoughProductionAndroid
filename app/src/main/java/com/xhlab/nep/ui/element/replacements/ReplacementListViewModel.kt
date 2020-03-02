package com.xhlab.nep.ui.element.replacements

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class ReplacementListViewModel @Inject constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    val replacementList = loadReplacementListUseCase.observeOnly(Resource.Status.SUCCESS)

    // Pair<ElementId, ElementType>
    private val _navigateToElementDetail = LiveEvent<Pair<Long, Int>>()
    val navigateToElementDetail: LiveData<Pair<Long, Int>>
        get() = _navigateToElementDetail

    fun init(oreDictName: String?) {
        requireNotNull(oreDictName) {
            "ore dict name not provided."
        }
        // ignore if recipe list is already loaded
        if (replacementList.value != null) {
            return
        }
        invokeMediatorUseCase(
            useCase = loadReplacementListUseCase,
            params = oreDictName
        )
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToElementDetail.value = elementId to elementType
    }
}