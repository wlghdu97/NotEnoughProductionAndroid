package com.xhlab.nep.shared.preference

import androidx.lifecycle.LiveData

interface GeneralPreference {
    val isFirstDBLoad: LiveData<Boolean>
    fun getFirstDBLoad(): Boolean

    val isDBLoaded: LiveData<Boolean>
    fun getDBLoaded(): Boolean
    fun setDBLoaded(value: Boolean)

    val isIconLoaded: LiveData<Boolean>
    fun getIconLoaded(): Boolean
    fun setIconLoaded(value: Boolean)

    val isDarkTheme: LiveData<Boolean?>
    fun getDarkTheme(): Boolean
    fun setDarkTheme(value: Boolean)
}