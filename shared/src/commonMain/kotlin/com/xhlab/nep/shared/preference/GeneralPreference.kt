package com.xhlab.nep.shared.preference

import kotlinx.coroutines.flow.StateFlow

interface GeneralPreference {
    val isFirstDBLoad: StateFlow<Boolean>
    fun getFirstDBLoad(): Boolean

    val isDBLoaded: StateFlow<Boolean>
    fun getDBLoaded(): Boolean
    fun setDBLoaded(value: Boolean)

    val isIconLoaded: StateFlow<Boolean>
    fun getIconLoaded(): Boolean
    fun setIconLoaded(value: Boolean)

    val isDarkTheme: StateFlow<Boolean?>
    fun getDarkTheme(): Boolean
    fun setDarkTheme(value: Boolean)

    val showDisconnectionAlert: StateFlow<Boolean>
    fun getShowDisconnectionAlert(): Boolean
    fun setShowDisconnectionAlert(value: Boolean)

    companion object {
        const val KEY_DB_FIRST_LOAD = "db_first_load"
        const val KEY_DB_STATUS = "db_load_status"
        const val KEY_ICON_STATUS = "icon_load_status"
        const val KEY_THEME = "theme"
        const val KEY_SHOW_DISCONNECTION_ALERT = "show_disconnection_alert"
    }
}
