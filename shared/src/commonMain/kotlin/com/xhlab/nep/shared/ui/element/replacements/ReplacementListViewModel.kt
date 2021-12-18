package com.xhlab.nep.shared.ui.element.replacements

import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ViewModel")
class ReplacementListViewModel constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    val replacementList = loadReplacementListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<Long>()
    val navigateToDetail: Flow<Long>
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

    override fun onClick(elementId: Long) {
        navigateToElementDetail(elementId)
    }

    private fun navigateToElementDetail(elementId: Long) {
        scope.launch {
            _navigateToDetail.emit(elementId)
        }
    }
}
