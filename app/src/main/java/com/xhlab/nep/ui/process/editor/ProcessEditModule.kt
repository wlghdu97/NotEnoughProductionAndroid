package com.xhlab.nep.ui.process.editor

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessEditModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessEditViewModel::class)
    abstract fun provideProcessEditViewModel(viewModel: ProcessEditViewModel): ViewModel
}