package com.xhlab.nep.shared.preference

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import platform.Foundation.NSUserDefaults

class GeneralUserDefaultsPreference : GeneralPreference {

    private val userDefaults = NSUserDefaults.standardUserDefaults

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
        return userDefaults.objectForKey(GeneralPreference.KEY_DB_FIRST_LOAD, true)
    }

    override fun getDBLoaded(): Boolean {
        return userDefaults.objectForKey(GeneralPreference.KEY_DB_STATUS, false)
    }

    override fun getIconLoaded(): Boolean {
        return userDefaults.objectForKey(GeneralPreference.KEY_ICON_STATUS, false)
    }

    override fun getDarkTheme(): Boolean {
        return userDefaults.objectForKey(GeneralPreference.KEY_THEME, false)
    }

    override fun getShowDisconnectionAlert(): Boolean {
        return userDefaults.objectForKey(GeneralPreference.KEY_SHOW_DISCONNECTION_ALERT, true)
    }

    override fun setDBLoaded(value: Boolean) {
        with(userDefaults) {
            setObject(value, GeneralPreference.KEY_DB_STATUS)
            setObject(false, GeneralPreference.KEY_DB_FIRST_LOAD)
            synchronize()
        }
        _isDBLoaded.value = value
    }

    override fun setIconLoaded(value: Boolean) {
        with(userDefaults) {
            setObject(value, GeneralPreference.KEY_ICON_STATUS)
            synchronize()
        }
        _isIconLoaded.value = value
    }

    override fun setDarkTheme(value: Boolean) {
        with(userDefaults) {
            setObject(value, GeneralPreference.KEY_THEME)
            synchronize()
        }
        _isDarkTheme.value = value
    }

    override fun setShowDisconnectionAlert(value: Boolean) {
        with(userDefaults) {
            setObject(value, GeneralPreference.KEY_SHOW_DISCONNECTION_ALERT)
            synchronize()
        }
        _showDisconnectionAlert.value = value
    }

    private inline fun <reified T> NSUserDefaults.objectForKey(key: String, default: T): T {
        val value = objectForKey(key)
        return value as? T ?: default
    }
}
