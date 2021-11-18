package com.xhlab.nep.shared.ui.element.usages

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.domain.recipe.LoadUsageListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ProvideWithDagger("ViewModel")
class UsageListViewModel constructor(
    private val loadUsageListUseCase: LoadUsageListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    val usageList = loadUsageListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    // Pair<ElementId, ElementType>
    private val _navigateToDetail = EventFlow<Pair<Long, Int>>()
    val navigateToDetail: Flow<Pair<Long, Int>>
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
            _navigateToDetail.emit(elementId to elementType)
        }
    }
}
