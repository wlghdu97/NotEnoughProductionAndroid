package com.xhlab.nep.ui.process.calculator

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessCalculationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessCalculationViewModel::class)
    abstract fun provideProcessCalculationViewModel(viewModel: ProcessCalculationViewModel): ViewModel
}