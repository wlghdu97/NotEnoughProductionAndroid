package com.xhlab.nep.ui.main.process.creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hadilq.liveevent.LiveEvent
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessCreationViewModel @Inject constructor(
    private val processRepo: ProcessRepo
) : ViewModel(), BaseViewModel by BasicViewModel() {

    private val _processName = MutableLiveData<String?>(null)
    val processName: LiveData<String?>
        get() = _processName

    private val _recipePair = MutableLiveData<Pair<Recipe, Element>>()
    val recipePair: LiveData<Pair<Recipe, Element>>
        get() = _recipePair

    private val _isNameValid = MediatorLiveData<Boolean?>()
    val isNameValid: LiveData<Boolean?>
        get() = _isNameValid

    private val _creationResult = LiveEvent<Resource<Unit>>()
    val creationResult: LiveData<Resource<Unit>>
        get() = _creationResult

    init {
        _isNameValid.addSource(processName) {
            _isNameValid.postValue(it?.isNotEmpty())
        }
    }

    fun changeProcessName(newName: String) {
        _processName.postValue(newName)
    }

    fun submitRecipe(recipe: Recipe, keyElement: Element) {
        _recipePair.postValue(recipe to keyElement)
    }

    fun createProcess() {
        val isNameValid = isNameValid.value
        val recipePair = recipePair.value
        if (isNameValid == true && recipePair != null) {
            val name = processName.value.toString().trim()
            launchSuspendFunction(_creationResult) {
                processRepo.createProcess(name, recipePair.first, recipePair.second)
            }
        } else if (isNameValid == null) {
            _isNameValid.postValue(false)
        } else {
            _creationResult.postValue(Resource.error(EmptyTargetRecipeException()))
        }
    }

    class EmptyTargetRecipeException : IllegalArgumentException()
}