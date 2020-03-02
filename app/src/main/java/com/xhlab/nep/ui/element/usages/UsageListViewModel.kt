package com.xhlab.nep.ui.element.usages

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.recipe.LoadUsageListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class UsageListViewModel @Inject constructor(
    private val loadUsageListUseCase: LoadUsageListUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    val usageList = loadUsageListUseCase.observeOnly(Resource.Status.SUCCESS)

    // Pair<ElementId, ElementType>
    private val _navigateToElementDetail = LiveEvent<Pair<Long, Int>>()
    val navigateToElementDetail: LiveData<Pair<Long, Int>>
        get() = _navigateToElementDetail

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        // ignore if usage list is already loaded
        if (usageList.value != null) {
            return
        }
        invokeMediatorUseCase(
            useCase = loadUsageListUseCase,
            params = elementId
        )
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToElementDetail.value = elementId to elementType
    }
}