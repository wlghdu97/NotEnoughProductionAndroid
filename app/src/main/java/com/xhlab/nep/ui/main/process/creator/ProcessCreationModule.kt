package com.xhlab.nep.ui.main.process.creator

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessCreationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessCreationViewModel::class)
    abstract fun provideProcessCreationViewModel(viewModel: ProcessCreationViewModel): ViewModel
}
