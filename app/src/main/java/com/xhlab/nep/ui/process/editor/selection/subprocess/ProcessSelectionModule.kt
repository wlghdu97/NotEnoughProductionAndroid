package com.xhlab.nep.ui.process.editor.selection.subprocess

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessSelectionModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessSelectionViewModel::class)
    abstract fun provideProcessSelectionViewModel(viewModel: ProcessSelectionViewModel): ViewModel
}
