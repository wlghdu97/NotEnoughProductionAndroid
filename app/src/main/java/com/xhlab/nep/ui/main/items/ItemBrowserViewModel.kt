package com.xhlab.nep.ui.main.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.item.ElementSearchUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.util.invokeMediatorUseCase
import com.xhlab.nep.ui.util.observeOnlySuccess
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemBrowserViewModel @Inject constructor(
    private val elementSearchUseCase: ElementSearchUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener {

    private val _elementSearchResult = elementSearchUseCase.observeOnlySuccess()
    val elementSearchResult = _elementSearchResult.transform {
        if (isDBLoaded.value) {
            emit(it)
        }
    }

    val isDBLoaded = generalPreference.isDBLoaded
    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToDetail = LiveEvent<ElementDetailNavigationUseCase.Parameters>()
    val navigateToDetail: LiveData<ElementDetailNavigationUseCase.Parameters>
        get() = _navigateToDetail

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun searchElements(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(50)
            invokeMediatorUseCase(
                useCase = elementSearchUseCase,
                params = ElementSearchUseCase.Parameter(term)
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
