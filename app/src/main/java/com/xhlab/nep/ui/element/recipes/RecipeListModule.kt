package com.xhlab.nep.ui.element.recipes

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class RecipeListModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecipeListViewModel::class)
    abstract fun bindRecipeListViewModel(viewModel: RecipeListViewModel): ViewModel
}