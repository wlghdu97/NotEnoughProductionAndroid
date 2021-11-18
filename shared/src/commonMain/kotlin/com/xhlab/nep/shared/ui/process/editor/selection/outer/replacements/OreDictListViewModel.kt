package com.xhlab.nep.shared.ui.process.editor.selection.outer.replacements

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.shared.domain.item.LoadOreDictListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.element.replacements.OreDictListener
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessViewModel")
class OreDictListViewModel constructor(
    private val loadOreDictListUseCase: LoadOreDictListUseCase
) : ViewModel(), OreDictListener {

    val oreDictNameList = loadOreDictListUseCase.observeOnlySuccess()

    private val _navigateToReplacementList = EventFlow<String>()
    val navigateToReplacementList: Flow<String>
        get() = _navigateToReplacementList.flow

    fun init(elementId: Long?) {
        requireNotNull(elementId) {
            "element id not provided."
        }
        invokeMediatorUseCase(
            useCase = loadOreDictListUseCase,
            params = elementId
        )
    }

    override fun onClicked(oreDictName: String) {
        scope.launch {
            _navigateToReplacementList.emit(oreDictName)
        }
    }
}
