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

    private val _isDBLoaded = MutableLiveData<Boolean>(getDBLoaded())
    override val isDBLoaded: LiveData<Boolean>
        get() = _isDBLoaded

    private val _isDarkTheme = MutableLiveData<Boolean?>(getDarkTheme())
    override val isDarkTheme: LiveData<Boolean?>
        get() = _isDarkTheme

    override fun getDBLoaded(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_db_status), false)
    }

    override fun getDarkTheme(): Boolean {
        return preference.getBoolean(app.getString(R.string.key_theme), false)
    }

    override fun setDBLoaded(value: Boolean) {
        preference.edit().putBoolean(app.getString(R.string.key_db_status), value).apply()
        _isDBLoaded.postValue(value)
    }

    override fun setDarkTheme(value: Boolean) {
        preference.edit().putBoolean(app.getString(R.string.key_theme), value).apply()
        _isDarkTheme.postValue(value)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            app.getString(R.string.key_db_status) ->
                _isDBLoaded.value = getDBLoaded()
            app.getString(R.string.key_theme) ->
                _isDarkTheme.value = getDarkTheme()
        }
    }
}