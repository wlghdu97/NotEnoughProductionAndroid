package com.xhlab.nep.shared.preference

import androidx.lifecycle.LiveData

interface GeneralPreference {
    val isDBLoaded: LiveData<Boolean>
    fun getDBLoaded(): Boolean
    fun setDBLoaded(value: Boolean)

    val isDarkTheme: LiveData<Boolean?>
    fun getDarkTheme(): Boolean
    fun setDarkTheme(value: Boolean)
}