package com.xhlab.nep.ui.process.editor.selection.outer.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
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