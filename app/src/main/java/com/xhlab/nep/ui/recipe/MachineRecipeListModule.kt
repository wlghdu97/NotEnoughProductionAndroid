package com.xhlab.nep.ui.recipe

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.shared.ui.recipe.MachineRecipeListViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class MachineRecipeListModule {
    @Binds
    @IntoMap
    @ViewModelKey(MachineRecipeListViewModel::class)
    abstract fun bindMachineRecipeListViewModel(viewModel: MachineRecipeListViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideMachineRecipeListFragment(): MachineRecipeListFragment
}
