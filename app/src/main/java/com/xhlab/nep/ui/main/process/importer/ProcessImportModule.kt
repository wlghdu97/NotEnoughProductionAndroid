package com.xhlab.nep.ui.main.process.importer

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.shared.ui.main.process.importer.ProcessImportViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessImportModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessImportViewModel::class)
    abstract fun provideProcessImportViewModel(viewModel: ProcessImportViewModel): ViewModel
}
