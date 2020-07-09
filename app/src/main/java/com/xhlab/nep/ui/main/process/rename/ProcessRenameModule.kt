package com.xhlab.nep.ui.main.process.rename

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessRenameModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessRenameViewModel::class)
    abstract fun provideProcessRenameViewModel(viewModel: ProcessRenameViewModel): ViewModel
}