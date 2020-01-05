package com.xhlab.nep.shared.preference

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

class GeneralSharedPreference @Inject constructor(app: Application) : GeneralPreference {

    private val preferenceChangeListener = { _: SharedPreferences?, key: String? ->
        if (key == DB_LOADED) {
            _isDBLoaded.value = getDBLoaded()
        }
    }

    private val preference: SharedPreferences by lazy {
        app.applicationContext.getSharedPreferences(GENERAL_PREFERENCE, MODE_PRIVATE).apply {
            registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        }
    }

    private val _isDBLoaded = MutableLiveData<Boolean>(getDBLoaded())
    override val isDBLoaded: LiveData<Boolean>
        get() = _isDBLoaded

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