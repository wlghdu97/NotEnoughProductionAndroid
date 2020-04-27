package com.xhlab.nep.ui.main.items

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xhlab.nep.domain.ElementDetailNavigationUseCase
import com.xhlab.nep.shared.domain.item.ElementSearchUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ItemBrowserViewModel @Inject constructor(
    private val elementSearchUseCase: ElementSearchUseCase,
    private val elementDetailNavigationUseCase: ElementDetailNavigationUseCase,
    generalPreference: GeneralPreference
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ElementListener
{
    private val _elementSearchResult = elementSearchUseCase.observeOnly(Resource.Status.SUCCESS)
    val elementSearchResult = Transformations.map(_elementSearchResult) {
        if (isDBLoaded.value == true) it else null
    }

    val isDBLoaded = generalPreference.isDBLoaded

    // to prevent DiffUtil's index out of bound
    private var searchDebounceJob: Job? = null

    fun searchElements(term: String) {
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(50)
            invokeMediatorUseCase(
                useCase = elementSearchUseCase,
                params = term
            )
        }
    }

    override fun onClick(elementId: Long, elementType: Int) {
        invokeUseCase(
            elementDetailNavigationUseCase,
            ElementDetailNavigationUseCase.Parameters(elementId, elementType)
        )
    }
}