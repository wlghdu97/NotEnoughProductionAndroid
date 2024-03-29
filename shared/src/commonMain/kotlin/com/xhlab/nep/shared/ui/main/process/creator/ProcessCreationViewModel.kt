package com.xhlab.nep.shared.ui.main.process.creator

import co.touchlab.kermit.Logger
import com.xhlab.nep.MR
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.util.StringResolver
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow

@ProvideWithDagger("ProcessViewModel")
class ProcessCreationViewModel constructor(
    private val processRepo: ProcessRepo,
    private val stringResolver: StringResolver
) : ViewModel() {

    private val _processName = MutableStateFlow<String?>(null)
    val processName = _processName.mapNotNull { it }

    private val _recipePair = MutableStateFlow<RecipeWithElement?>(null)
    val recipePair = _recipePair.mapNotNull { it }

    private val _isNameValid = MutableStateFlow<Boolean?>(null)
    val isNameValid = _isNameValid.mapNotNull { it }

    private val _creationErrorMessage = EventFlow<String>()
    val creationErrorMessage: Flow<String>
        get() = _creationErrorMessage.flow

    private val _dismiss = EventFlow<Unit>()
    val dismiss: Flow<Unit>
        get() = _dismiss.flow

    init {
        scope.launch {
            processName.collect {
                _isNameValid.value = it.isNotEmpty()
            }
        }
    }

    fun changeProcessName(newName: String) {
        _processName.value = newName
    }

    fun submitRecipe(recipe: Recipe, keyElement: RecipeElement) {
        _recipePair.value = RecipeWithElement(recipe, keyElement)
    }

    fun createProcess() {
        val isNameValid = _isNameValid.value
        val recipePair = _recipePair.value
        if (isNameValid == true && recipePair != null) {
            val handler = CoroutineExceptionHandler { _, throwable ->
                Logger.e("Failed to create process ${throwable.stackTraceToString()}", throwable)
                scope.launch {
                    _creationErrorMessage.emit(stringResolver.getString(MR.strings.error_failed_to_create_process))
                }
            }
            scope.launch(handler) {
                val name = _processName.value.toString().trim()
                processRepo.createProcess(name, recipePair.recipe, recipePair.element)
                _dismiss.emit(Unit)
            }
        } else if (isNameValid == null) {
            _isNameValid.value = false
        } else {
            scope.launch {
                _creationErrorMessage.emit(stringResolver.getString(MR.strings.error_empty_target_recipe))
            }
        }
    }

    data class RecipeWithElement(val recipe: Recipe, val element: RecipeElement)
}
