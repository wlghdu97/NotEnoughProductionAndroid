package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.item.CheckReplacementListCountUseCase
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.invokeMediatorUseCase
import com.xhlab.nep.ui.util.observeOnlySuccess
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReplacementListViewModel @Inject constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    private val checkReplacementListCountUseCase: CheckReplacementListCountUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ElementListener {

    private val oreDictName = MutableLiveData<String>()

    val replacementList = loadReplacementListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToRecipeList = LiveEvent<Long>()
    val navigateToRecipeList: LiveData<Long>
        get() = _navigateToRecipeList

    private val _navigateToRecipeListWithKey = LiveEvent<String>()
    val navigateToRecipeListWithKey: LiveData<String>
        get() = _navigateToRecipeListWithKey

    init {
        viewModelScope.launch {
            oreDictName.asFlow().collectLatest {
                invokeMediatorUseCase(
                    useCase = loadReplacementListUseCase,
                    params = it
                )
            }
        }
        viewModelScope.launch {
            replacementList.collectLatest {
                val name = oreDictName.value
                if (name != null) {
                    val count = checkReplacementListCountUseCase.invokeInstant(
                        CheckReplacementListCountUseCase.Parameter(name)
                    ).data ?: 0
                    if (count < 1) {
                        _navigateToRecipeListWithKey.postValue(oreDictName.value)
                    }
                }
            }
        }
    }

    fun init(oreDictName: String?) {
        requireNotNull(oreDictName) {
            "ore dict name not provided."
        }
        // ignore if recipe list is already loaded
        if (loadReplacementListUseCase.observe().value.data != null) {
            return
        }
        this.oreDictName.value = oreDictName
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToRecipeList.postValue(elementId)
    }
}
