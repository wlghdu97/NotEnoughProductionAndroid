package com.xhlab.nep.ui.delegate

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.util.Theme
import javax.inject.Inject

class ThemeDelegateImpl @Inject constructor(
    generalPreference: GeneralPreference
) : ThemeDelegate {

    private val _currentThemeObservable = Transformations.map(generalPreference.isDarkTheme) {
        if (it != null) getCurrentTheme(it) else Theme.SYSTEM
    }
    override val currentThemeObservable: LiveData<Theme>
        get() = _currentThemeObservable

    override val currentTheme = getCurrentTheme(generalPreference.getDarkTheme())

    private fun getCurrentTheme(isDarkTheme: Boolean) = when (isDarkTheme) {
        true -> Theme.DARK
        false -> Theme.LIGHT
    }
}
