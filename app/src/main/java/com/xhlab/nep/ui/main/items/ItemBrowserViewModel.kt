package com.xhlab.nep.ui.main.items

import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.item.ElementSearchUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.invokeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemBrowserViewModel @Inject constructor(
    private val elementSearchUseCase: ElementSearchUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    private val _elementSearchResult = elementSearchUseCase.observeOnlySuccess()
    val elementSearchResult = _elementSearchResult.transform {
        if (isDBLoaded.value) {
            emit(it)
        }
    }

    val isDBLoaded = generalPreference.isDBLoaded
    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = EventFlow<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: Flow<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail.flow

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun searchElements(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = scope.launch {
            delay(50)
            invokeMediatorUseCase(
                useCase = elementSearchUseCase,
                params = ElementSearchUseCase.Parameter(term)
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
