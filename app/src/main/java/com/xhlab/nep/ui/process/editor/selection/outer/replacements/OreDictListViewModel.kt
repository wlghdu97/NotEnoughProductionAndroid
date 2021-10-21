package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.shared.domain.item.LoadOreDictListUseCase
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.element.replacements.OreDictListener
import com.xhlab.nep.ui.util.invokeMediatorUseCase
import com.xhlab.nep.ui.util.observeOnlySuccess
import kotlinx.coroutines.launch
import javax.inject.Inject

class OreDictListViewModel @Inject constructor(
    private val loadOreDictListUseCase: LoadOreDictListUseCase
) : ViewModel(), BaseViewModel by BasicViewModel(), OreDictListener {

    val oreDictNameList = loadOreDictListUseCase.observeOnlySuccess()

    private val _navigateToReplacementList = LiveEvent<String>()
    val navigateToReplacementList: LiveData<String>
        get() = _navigateToReplacementList

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        viewModelScope.launch {
            invokeMediatorUseCase(
                useCase = loadOreDictListUseCase,
                params = elementId
            )
        }
    }

    override fun onClicked(oreDictName: String) {
        _navigateToReplacementList.value = oreDictName
    }
}
