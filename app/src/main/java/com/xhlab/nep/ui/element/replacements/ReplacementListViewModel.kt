package com.xhlab.nep.ui.element.replacements

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import com.xhlab.nep.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReplacementListViewModel @Inject constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    val replacementList = loadReplacementListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: Flow<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail.flow

    fun init(oreDictName: String?) {
        requireNotNull(oreDictName) {
            "ore dict name not provided."
        }
        // ignore if recipe list is already loaded
        if (loadReplacementListUseCase.observe().value.data != null) {
            return
        }
        scope.launch {
            invokeMediatorUseCase(
                useCase = loadReplacementListUseCase,
                params = oreDictName
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
