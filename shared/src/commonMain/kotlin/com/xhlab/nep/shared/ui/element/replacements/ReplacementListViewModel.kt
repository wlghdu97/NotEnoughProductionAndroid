package com.xhlab.nep.shared.ui.element.replacements

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ProvideWithDagger("ViewModel")
class ReplacementListViewModel constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    val replacementList = loadReplacementListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    // Pair<ElementId, ElementType>
    private val _navigateToDetail = EventFlow<Pair<Long, Int>>()
    val navigateToDetail: Flow<Pair<Long, Int>>
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
        navigateToElementDetail(elementId, elementType)
    }

    private fun navigateToElementDetail(elementId: Long, elementType: Int) {
        scope.launch {
            _navigateToDetail.emit(elementId to elementType)
        }
    }
}
