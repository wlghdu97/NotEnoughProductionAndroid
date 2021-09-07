package com.xhlab.nep.ui.delegate

import androidx.lifecycle.LiveData
import com.xhlab.nep.util.Theme

interface ThemeDelegate {
    val currentThemeObservable: LiveData<Theme>
    val currentTheme: Theme
}
