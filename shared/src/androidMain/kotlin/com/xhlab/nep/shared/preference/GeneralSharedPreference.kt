package com.xhlab.nep.shared.preference

import android.app.Application
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GeneralSharedPreference @Inject constructor(
    app: Application
) : GeneralPreference, SharedPreferences.OnSharedPreferenceChangeListener {

    private val preference = PreferenceManager
        .getDefaultSharedPreferences(app.applicationContext).apply {
            registerOnSharedPreferenceChangeListener(this@GeneralSharedPreference)
        }

    private val _isFirstDBLoad = MutableStateFlow(getFirstDBLoad())
    override val isFirstDBLoad: StateFlow<Boolean>
        get() = _isFirstDBLoad

    private val _isDBLoaded = MutableStateFlow(getDBLoaded())
    override val isDBLoaded: StateFlow<Boolean>
        get() = _isDBLoaded

    private val _isIconLoaded = MutableStateFlow(getIconLoaded())
    override val isIconLoaded: StateFlow<Boolean>
        get() = _isIconLoaded

    private val _isDarkTheme = MutableStateFlow(getDarkTheme())
    override val isDarkTheme: StateFlow<Boolean?>
        get() = _isDarkTheme

    private val _showDisconnectionAlert = MutableStateFlow(getShowDisconnectionAlert())
    override val showDisconnectionAlert: StateFlow<Boolean>
        get() = _showDisconnectionAlert

    override fun getFirstDBLoad(): Boolean {
        return preference.getBoolean(GeneralPreference.KEY_DB_FIRST_LOAD, true)
    }

    override fun getDBLoaded(): Boolean {
        return preference.getBoolean(GeneralPreference.KEY_DB_STATUS, false)
    }

    override fun getIconLoaded(): Boolean {
        return preference.getBoolean(GeneralPreference.KEY_ICON_STATUS, false)
    }

    override fun getDarkTheme(): Boolean {
        return preference.getBoolean(GeneralPreference.KEY_THEME, false)
    }

    override fun getShowDisconnectionAlert(): Boolean {
        return preference.getBoolean(GeneralPreference.KEY_SHOW_DISCONNECTION_ALERT, true)
    }

    override fun setDBLoaded(value: Boolean) {
        preference.edit()
            .putBoolean(GeneralPreference.KEY_DB_STATUS, value)
            .putBoolean(GeneralPreference.KEY_DB_FIRST_LOAD, false)
            .apply()
        _isDBLoaded.value = value
    }

    override fun setIconLoaded(value: Boolean) {
        preference.edit().putBoolean(GeneralPreference.KEY_ICON_STATUS, value).apply()
        _isIconLoaded.value = value
    }

    override fun setDarkTheme(value: Boolean) {
        preference.edit().putBoolean(GeneralPreference.KEY_THEME, value).apply()
        _isDarkTheme.value = value
    }

    override fun setShowDisconnectionAlert(value: Boolean) {
        preference.edit().putBoolean(GeneralPreference.KEY_SHOW_DISCONNECTION_ALERT, value)
            .apply()
        _showDisconnectionAlert.value = value
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            GeneralPreference.KEY_DB_FIRST_LOAD ->
                _isFirstDBLoad.value = getFirstDBLoad()
            GeneralPreference.KEY_DB_STATUS ->
                _isDBLoaded.value = getDBLoaded()
            GeneralPreference.KEY_ICON_STATUS ->
                _isIconLoaded.value = getIconLoaded()
            GeneralPreference.KEY_THEME ->
                _isDarkTheme.value = getDarkTheme()
            GeneralPreference.KEY_SHOW_DISCONNECTION_ALERT ->
                _showDisconnectionAlert.value = getShowDisconnectionAlert()
        }
    }
}
