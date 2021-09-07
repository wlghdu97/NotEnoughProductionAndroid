package com.xhlab.nep.ui.element

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.element.recipes.RecipeListFragment
import com.xhlab.nep.ui.element.recipes.RecipeListModule
import com.xhlab.nep.ui.element.replacements.ReplacementContainerFragment
import com.xhlab.nep.ui.element.replacements.ReplacementContainerModule
import com.xhlab.nep.ui.element.usages.UsageListFragment
import com.xhlab.nep.ui.element.usages.UsageListModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [RecipeListModule::class, ReplacementContainerModule::class, UsageListModule::class])
@Suppress("unused")
abstract class ElementDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(ElementDetailViewModel::class)
    abstract fun bindElementDetailViewModel(viewModel: ElementDetailViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideElementDetailFragment(): ElementDetailFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideRecipeListFragment(): RecipeListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideReplacementContainerFragment(): ReplacementContainerFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideUsageListFragment(): UsageListFragment
}
