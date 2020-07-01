package com.xhlab.nep.shared.preference

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.xhlab.nep.shared.R
import javax.inject.Inject

class GeneralSharedPreference @Inject constructor(
    private val app: Application
) : GeneralPreference, SharedPreferences.OnSharedPreferenceChangeListener {

    private val preference = PreferenceManager
        .getDefaultSharedPreferences(app.applicationContext).apply {
            registerOnSharedPreferenceChangeListener(this@GeneralSharedPreference)
        }

    private val _isFirstDBLoad = MutableLiveData<Boolean>(getFirstDBLoad())
    override val isFirstDBLoad: LiveData<Boolean>
        get() = _isFirstDBLoad

    private val _isDBLoaded = MutableLiveData<Boolean>(getDBLoaded())
    override val isDBLoaded: LiveData<Boolean>
        get() = _isDBLoaded

    private val _isIconLoaded = MutableLiveData<Boolean>(getIconLoaded())
    override val isIconLoaded: LiveData<Boolean>
        get() = _isIconLoaded

    private val _isDarkTheme = MutableLiveData<Boolean?>(getDarkTheme())
    override val isDarkTheme: LiveData<Boolean?>
        get() = _isDarkTheme

    private val _showDisconnectionAlert = MutableLiveData<Boolean>(getShowDisconnectionAlert())
    override val showDisconnectionAlert: LiveData<Boolean>
        get() = _showDisconnectionAlert

    override fun getFirstDBLoad(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_db_first_load), true)
    }

    override fun getDBLoaded(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_db_status), false)
    }

    override fun getIconLoaded(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_icon_status), false)
    }

    override fun getDarkTheme(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_theme), false)
    }

    override fun getShowDisconnectionAlert(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_show_disconnection_alert), true)
    }

    override fun setDBLoaded(value: Boolean) {
        preference.edit()
            .putBoolean(app.getString(R.string.key_db_status), value)
            .putBoolean(app.getString(R.string.key_db_first_load), false)
            .apply()
        _isDBLoaded.postValue(value)
    }

    override fun setIconLoaded(value: Boolean) {
        preference.edit().putBoolean(app.getString(R.string.key_icon_status), value).apply()
        _isIconLoaded.postValue(value)
    }

    override fun setDarkTheme(value: Boolean) {
        preference.edit().putBoolean(app.getString(R.string.key_theme), value).apply()
        _isDarkTheme.postValue(value)
    }

    override fun setShowDisconnectionAlert(value: Boolean) {
        preference.edit().putBoolean(app.getString(R.string.key_show_disconnection_alert), value).apply()
        _showDisconnectionAlert.postValue(value)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            app.getString(R.string.key_db_first_load) ->
                _isFirstDBLoad.value = getFirstDBLoad()
            app.getString(R.string.key_db_status) ->
                _isDBLoaded.value = getDBLoaded()
            app.getString(R.string.key_icon_status) ->
                _isIconLoaded.value = getIconLoaded()
            app.getString(R.string.key_theme) ->
                _isDarkTheme.value = getDarkTheme()
            app.getString(R.string.key_show_disconnection_alert) ->
                _showDisconnectionAlert.value = getShowDisconnectionAlert()
        }
    }
}