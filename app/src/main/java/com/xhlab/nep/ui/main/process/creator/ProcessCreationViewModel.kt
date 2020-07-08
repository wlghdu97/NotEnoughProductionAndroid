package com.xhlab.nep.ui.main.process.creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import javax.inject.Inject

class ProcessCreationViewModel @Inject constructor(

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

    }
}