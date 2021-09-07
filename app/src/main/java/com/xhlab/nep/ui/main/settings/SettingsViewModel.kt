package com.xhlab.nep.ui.main.settings

import androidx.lifecycle.ViewModel
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.BaseViewModel
import com.xhlab.nep.ui.BasicViewModel
import com.xhlab.nep.ui.delegate.ThemeDelegate
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val generalPreference: GeneralPreference,
    themeDelegate: ThemeDelegate
) : ViewModel(),
    BaseViewModel by BasicViewModel(),
    ThemeDelegate by themeDelegate {
    val isFirstDBLoad = generalPreference.isFirstDBLoad
    val isDBLoaded = generalPreference.isDBLoaded
    val isIconLoaded = generalPreference.isIconLoaded

    fun setDBLoaded(newValue: Boolean) {
        generalPreference.setDBLoaded(newValue)
    }

    fun setIconLoaded(newValue: Boolean) {
        generalPreference.setIconLoaded(newValue)
    }
}
