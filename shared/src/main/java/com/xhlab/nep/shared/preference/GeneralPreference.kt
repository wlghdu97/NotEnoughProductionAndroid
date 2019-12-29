package com.xhlab.nep.shared.preference

import android.app.Application
import android.content.Context.MODE_PRIVATE
import javax.inject.Inject

class GeneralPreference @Inject constructor(app: Application) {

    private val preference
            = app.applicationContext.getSharedPreferences(GENERAL_PREFERENCE, MODE_PRIVATE)

    var isDBLoaded: Boolean
        get() = preference.getBoolean(DB_LOADED, false)
        set(value) { preference.edit().putBoolean(DB_LOADED, value).apply() }

    companion object {
        private const val GENERAL_PREFERENCE = "general_preference"
        private const val DB_LOADED = "isDBLoaded"
    }
}