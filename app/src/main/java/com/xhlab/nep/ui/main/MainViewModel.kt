package com.xhlab.nep.ui.main

import androidx.lifecycle.ViewModel
import com.xhlab.nep.ui.delegate.ThemeDelegate
import javax.inject.Inject

class MainViewModel @Inject constructor(
    themeDelegate: ThemeDelegate
) : ViewModel(), ThemeDelegate by themeDelegate