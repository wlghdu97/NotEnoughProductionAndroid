package com.xhlab.nep.ui.main.machines.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class MachineResultModule {
    @Binds
    @IntoMap
    @ViewModelKey(MachineResultViewModel::class)
    abstract fun provideMachineResultViewModel(viewModel: MachineResultViewModel): ViewModel
}