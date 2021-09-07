package com.xhlab.nep.ui.main

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.dialogs.JsonParseDialog
import com.xhlab.nep.ui.element.ElementDetailModule
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.items.ItemBrowserModule
import com.xhlab.nep.ui.main.machines.MachineBrowserFragment
import com.xhlab.nep.ui.main.machines.MachineBrowserModule
import com.xhlab.nep.ui.main.machines.details.MachineResultModule
import com.xhlab.nep.ui.main.process.ProcessListFragment
import com.xhlab.nep.ui.main.process.ProcessListModule
import com.xhlab.nep.ui.main.settings.SettingsFragment
import com.xhlab.nep.ui.main.settings.SettingsModule
import com.xhlab.nep.ui.recipe.MachineRecipeListModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(
    includes = [
        ElementDetailModule::class,
        MachineRecipeListModule::class,
        MachineResultModule::class
    ]
)
@Suppress("unused")
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(viewModel: MainViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector(modules = [ItemBrowserModule::class])
    abstract fun provideItemBrowserFragment(): ItemBrowserFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MachineBrowserModule::class])
    abstract fun provideMachineBrowserFragment(): MachineBrowserFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProcessListModule::class])
    abstract fun provideProcessListFragment(): ProcessListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun provideSettingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideJsonParserDialog(): JsonParseDialog
}
