package com.xhlab.nep.ui.element.usages

import androidx.lifecycle.ViewModel
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageListUseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class UsageListViewModel @Inject constructor(
    private val loadUsageListUseCase: LoadUsageListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    val usageList = loadUsageListUseCase.observeOnly(Resource.Status.SUCCESS)

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
        invokeUseCase(
            elementDetailNavigationUseCase,
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }
}