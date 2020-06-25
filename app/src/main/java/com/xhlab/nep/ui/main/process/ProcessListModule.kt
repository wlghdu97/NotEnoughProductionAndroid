package com.xhlab.nep.ui.main.process

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessListModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessListViewModel::class)
    abstract fun provideProcessListViewModel(viewModel: ProcessListViewModel): ViewModel
}