package com.xhlab.nep.shared.ui.main.settings

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.multiplatform.util.EventFlow
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.shared.domain.icon.IconUnzipUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.util.ZipArchiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

@ProvideWithDagger("SettingsViewModel")
class SettingsViewModel constructor(
    private val iconUnzipUseCase: IconUnzipUseCase,
    private val generalPreference: GeneralPreference,
) : ViewModel() {

    val isFirstDBLoad = generalPreference.isFirstDBLoad
    val isDBLoaded = generalPreference.isDBLoaded
    val isIconLoaded = generalPreference.isIconLoaded

    private val iconUnzipResult = iconUnzipUseCase.observe()
    val iconUnzipLog = iconUnzipResult.mapNotNull { it.data }
    val isUnzipping = iconUnzipResult.map {
        it.data != null && it.status == Resource.Status.LOADING
    }

    private val _showIconZipPicker = EventFlow<Unit>()
    val showIconZipPicker: Flow<Unit>
        get() = _showIconZipPicker.flow

    fun toggleIconLoaded() {
        scope.launch {
            if (generalPreference.getIconLoaded()) {
                generalPreference.setIconLoaded(false)
            } else {
                _showIconZipPicker.emit(Unit)
            }
        }
    }

    fun reloadIcons(archiver: ZipArchiver) {
        invokeMediatorUseCase(iconUnzipUseCase, archiver)
    }

    fun isIconLoaded() = generalPreference.getIconLoaded()
}
