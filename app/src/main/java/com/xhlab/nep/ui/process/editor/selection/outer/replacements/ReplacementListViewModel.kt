package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.main.items.ElementListener
import javax.inject.Inject

class ReplacementListViewModel @Inject constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), ElementListener {

    private val oreDictName = MutableLiveData<String>()

    val replacementList = loadReplacementListUseCase.observeOnly(Resource.Status.SUCCESS)

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToRecipeList = MediatorLiveData<Long>()
    val navigateToRecipeList: LiveData<Long>
        get() = _navigateToRecipeList

    private val _navigateToRecipeListWithKey = MediatorLiveData<String>()
    val navigateToRecipeListWithKey: LiveData<String>
        get() = _navigateToRecipeListWithKey

    init {
        loadReplacementListUseCase.observe().addSource(oreDictName) {
            invokeMediatorUseCase(
                useCase = loadReplacementListUseCase,
                params = it
            )
        }
        _navigateToRecipeList.addSource(replacementList) {
            if (it != null && it.isEmpty()) {
                _navigateToRecipeListWithKey.postValue(oreDictName.value)
            }
        }
    }

    fun init(oreDictName: String?) {
        requireNotNull(oreDictName) {
            "ore dict name not provided."
        }
        // ignore if recipe list is already loaded
        if (replacementList.value != null) {
            return
        }
        this.oreDictName.value = oreDictName
    }

    override fun onClick(elementId: Long, elementType: Int) {
        _navigateToRecipeList.postValue(elementId)
    }
}