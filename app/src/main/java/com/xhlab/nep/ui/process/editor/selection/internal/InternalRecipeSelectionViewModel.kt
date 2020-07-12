package com.xhlab.nep.ui.process.editor.selection.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.LoadProcessUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import javax.inject.Inject

class InternalRecipeSelectionViewModel @Inject constructor(
    private val processRepo: ProcessRepo,
    private val loadProcessUseCase: LoadProcessUseCase,
    generalPreference: GeneralPreference
) : ViewModel(), BaseViewModel by BasicViewModel(), RecipeSelectionListener {

    val process = loadProcessUseCase.observeOnly(Resource.Status.SUCCESS)

    private val _constraint = MutableLiveData<ProcessEditViewModel.ConnectionConstraint>()
    val constraint: LiveData<ProcessEditViewModel.ConnectionConstraint>
        get() = _constraint

    val isIconLoaded = generalPreference.isIconLoaded

    private val _iconMode = MutableLiveData<Boolean>(true)
    val iconMode: LiveData<Boolean>
        get() = _iconMode

    private val _connectionResult = LiveEvent<Resource<Unit>>()
    val connectionResult: LiveData<Resource<Unit>>
        get() = _connectionResult

    fun init(constraint: ProcessEditViewModel.ConnectionConstraint?) {
        requireNotNull(constraint)
        invokeMediatorUseCase(
            useCase = loadProcessUseCase,
            params = LoadProcessUseCase.Parameter(constraint.processId)
        )
        _constraint.postValue(constraint)
    }

    private fun requireProcessId()
            = process.value?.id ?: throw NullPointerException("process id is null.")

    override fun onSelect(from: Recipe, to: Recipe, element: Element, reversed: Boolean) {
        launchSuspendFunction(_connectionResult) {
            processRepo.connectRecipe(requireProcessId(), from, to, element, reversed)
        }
    }

    fun toggleIconMode() {
        _iconMode.postValue(_iconMode.value != true)
    }
}