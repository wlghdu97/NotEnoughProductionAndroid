package com.xhlab.nep.ui.element.usages

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.recipe.LoadUsageListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.invokeMediatorUseCase
import com.xhlab.nep.ui.util.observeOnlySuccess
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsageListViewModel @Inject constructor(
    private val loadUsageListUseCase: LoadUsageListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener {

    val usageList = loadUsageListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = LiveEvent<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: LiveData<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        // ignore if usage list is already loaded
        if (loadUsageListUseCase.observe().value.data != null) {
            return
        }
        viewModelScope.launch {
            invokeMediatorUseCase(
                useCase = loadUsageListUseCase,
                params = elementId
            )
        }
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToDetail.postValue(
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }
}
