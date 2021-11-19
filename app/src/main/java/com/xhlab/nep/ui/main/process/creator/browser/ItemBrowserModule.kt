package com.xhlab.nep.ui.main.process.creator.browser

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.shared.ui.main.process.creator.browser.ProcessItemBrowserViewModel
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.items.ItemBrowserModule
import com.xhlab.nep.ui.main.process.creator.browser.details.MachineRecipeListFragment
import com.xhlab.nep.ui.main.process.creator.browser.details.MachineRecipeListModule
import com.xhlab.nep.ui.main.process.creator.browser.recipes.RecipeListFragment
import com.xhlab.nep.ui.main.process.creator.browser.recipes.RecipeListModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ItemBrowserModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessItemBrowserViewModel::class)
    abstract fun provideProcessItemBrowserViewModel(viewModel: ProcessItemBrowserViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector(modules = [ItemBrowserModule::class])
    abstract fun provideItemBrowserFragment(): ItemBrowserFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RecipeListModule::class])
    abstract fun provideRecipeListFragment(): RecipeListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MachineRecipeListModule::class])
    abstract fun provideMachineRecipeListFragment(): MachineRecipeListFragment
}
