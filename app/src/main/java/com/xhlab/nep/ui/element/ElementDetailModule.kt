package com.xhlab.nep.ui.element

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.element.recipes.RecipeListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ElementDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(ElementDetailViewModel::class)
    abstract fun bindElementDetailViewModel(viewModel: ElementDetailViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideRecipeListFragment(): RecipeListFragment
}