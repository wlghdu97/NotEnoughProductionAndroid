package com.xhlab.nep.ui.main.process.creator.browser.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.shared.ui.main.process.creator.browser.details.MachineRecipeListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class MachineRecipeListModule {
    @Binds
    @IntoMap
    @ViewModelKey(MachineRecipeListViewModel::class)
    abstract fun provideMachineRecipeListViewModel(viewModel: MachineRecipeListViewModel): ViewModel
}
