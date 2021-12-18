package com.xhlab.nep.shared.ui.main.settings

import com.xhlab.nep.shared.domain.icon.IconUnzipUseCase
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.ui.ViewModel
import com.xhlab.nep.shared.ui.invokeMediatorUseCase
import com.xhlab.nep.shared.util.ZipArchiver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger
import kr.sparkweb.multiplatform.util.EventFlow
import kr.sparkweb.multiplatform.util.Resource

@ProvideWithDagger("SettingsViewModel")
class SettingsViewModel constructor(
    private val parseRecipeUseCase: ParseRecipeUseCase,
    private val iconUnzipUseCase: IconUnzipUseCase,
    private val generalPreference: GeneralPreference,
) : ViewModel() {

    val isFirstDBLoad = generalPreference.isFirstDBLoad
    val isDBLoaded = generalPreference.isDBLoaded
    val isIconLoaded = generalPreference.isIconLoaded

    private val parseRecipeResult = parseRecipeUseCase.observe()
    val parseRecipeLog = parseRecipeResult.mapNotNull { it.data }
    val isParsing = parseRecipeResult.map {
        it.data != null && it.status == Resource.Status.LOADING
    }

    private val iconUnzipResult = iconUnzipUseCase.observe()
    val iconUnzipLog = iconUnzipResult.mapNotNull { it.data }
    val isUnzipping = iconUnzipResult.map {
        it.data != null && it.status == Resource.Status.LOADING
    }

    private val _showJsonPicker = EventFlow<Unit>()
    val showJsonPicker: Flow<Unit>
        get() = _showJsonPicker.flow

    private val _showIconZipPicker = EventFlow<Unit>()
    val showIconZipPicker: Flow<Unit>
        get() = _showIconZipPicker.flow

    fun toggleDBLoaded() {
        scope.launch {
            if (generalPreference.getDBLoaded()) {
                generalPreference.setDBLoaded(false)
            } else {
                _showJsonPicker.emit(Unit)
            }
        }
    }

    fun toggleIconLoaded() {
        scope.launch {
            if (generalPreference.getIconLoaded()) {
                generalPreference.setIconLoaded(false)
            } else {
                _showIconZipPicker.emit(Unit)
            }
        }
    }

    fun parseRecipes(reader: () -> JsonReader) {
        invokeMediatorUseCase(parseRecipeUseCase, reader)
    }

    fun reloadIcons(archiver: ZipArchiver) {
        invokeMediatorUseCase(iconUnzipUseCase, archiver)
    }

    fun isDBLoaded() = generalPreference.getDBLoaded()

    fun isIconLoaded() = generalPreference.getIconLoaded()
}
