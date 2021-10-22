package com.xhlab.nep.ui.element.usages

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadUsageListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class UsageListViewModel @Inject constructor(
    private val loadUsageListUseCase: LoadUsageListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    val usageList = loadUsageListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: Flow<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail.flow

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        // ignore if usage list is already loaded
        if (loadUsageListUseCase.observe().value.data != null) {
            return
        }
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadUsageListUseCase,
                params = elementId
            )
        }
    }

    override fun onClick(elementId: Long, elementType: Int) {
        scope.launch {
            _navigateToDetail.emit(
                ElementDetailNavigationUseCase.Parameters(elementId, elementType)
            )
        }
    }

    fun navigateToElementDetail(params: ElementDetailNavigationUseCase.Parameters) {
        invokeUseCase(
            useCase = elementDetailNavigationUseCase,
            params = params
        )
    }
}
