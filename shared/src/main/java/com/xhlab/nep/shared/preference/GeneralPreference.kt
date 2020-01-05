package com.xhlab.nep.shared.preference

import androidx.lifecycle.LiveData

interface GeneralPreference {
    val isDBLoaded: LiveData<Boolean>
    fun getDBLoaded(): Boolean
    fun setDBLoaded(value: Boolean)
}