package com.xhlab.nep.shared.ui.process.editor.selection.outer.replacements

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.nep.shared.domain.item.CheckReplacementListCountUseCase
import com.xhlab.nep.shared.domain.item.LoadReplacementListUseCase
import com.xhlab.nep.shared.domain.observeOnlySuccess
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.ui.main.items.ElementListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ProvideWithDagger("ProcessEditorViewModel")
class ReplacementListViewModel constructor(
    private val loadReplacementListUseCase: LoadReplacementListUseCase,
    private val checkReplacementListCountUseCase: CheckReplacementListCountUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), ElementListener {

    private val oreDictName = MutableStateFlow<String?>(null)

    val replacementList = loadReplacementListUseCase.observeOnlySuccess()

    val isIconLoaded = generalPreference.isIconLoaded

    private val _navigateToRecipeList = EventFlow<Long>()
    val navigateToRecipeList: Flow<Long>
        get() = _navigateToRecipeList.flow

    private val _navigateToRecipeListWithKey = EventFlow<String>()
    val navigateToRecipeListWithKey: Flow<String>
        get() = _navigateToRecipeListWithKey.flow

    init {
        scope.launch {
            oreDictName.collectLatest {
                if (it != null) {
                    invokeMediatorUseCase(
                        useCase = loadReplacementListUseCase,
                        params = it
                    )
                }
            }
        }

        scope.launch {
            replacementList.collectLatest {
                val name = oreDictName.value
                if (name != null) {
                    val count = checkReplacementListCountUseCase.invokeInstant(
                        CheckReplacementListCountUseCase.Parameter(name)
                    ).data ?: 0
                    if (count < 1) {
                        _navigateToRecipeListWithKey.emit(name)
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

    override fun onClick(elementId: Long) {
        scope.launch {
            _navigateToRecipeList.emit(elementId)
        }
    }
}
