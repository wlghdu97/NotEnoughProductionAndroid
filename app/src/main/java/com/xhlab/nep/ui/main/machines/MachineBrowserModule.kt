package com.xhlab.nep.ui.main.machines

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class MachineBrowserModule {
    @Binds
    @IntoMap
    @ViewModelKey(MachineBrowserViewModel::class)
    abstract fun provideMachineBrowserViewModel(viewModel: MachineBrowserViewModel): ViewModel
}
