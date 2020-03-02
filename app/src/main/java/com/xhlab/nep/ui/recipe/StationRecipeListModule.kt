package com.xhlab.nep.ui.recipe

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class StationRecipeListModule {
    @Binds
    @IntoMap
    @ViewModelKey(StationRecipeListViewModel::class)
    abstract fun bindStationRecipeListViewModel(viewModel: StationRecipeListViewModel): ViewModel
}