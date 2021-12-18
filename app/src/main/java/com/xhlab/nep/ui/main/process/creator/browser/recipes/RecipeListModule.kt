package com.xhlab.nep.ui.main.process.creator.browser.recipes

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.shared.ui.main.process.creator.browser.recipes.RecipeListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class RecipeListModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecipeListViewModel::class)
    abstract fun provideRecipeListViewModel(viewModel: RecipeListViewModel): ViewModel
}
