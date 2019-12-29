package com.xhlab.nep.shared.preference

import android.app.Application
import android.content.Context.MODE_PRIVATE
import javax.inject.Inject

class GeneralSharedPreference @Inject constructor(app: Application) : GeneralPreference {

    private val preference
            = app.applicationContext.getSharedPreferences(GENERAL_PREFERENCE, MODE_PRIVATE)

    override fun getDBLoaded(): Boolean {
        return preference.getBoolean(DB_LOADED, false)
    }

    override fun setDBLoaded(value: Boolean) {
        preference.edit().putBoolean(DB_LOADED, value).apply()
    }

    companion object {
        private const val GENERAL_PREFERENCE = "general_preference"
        private const val DB_LOADED = "isDBLoaded"
    }
}