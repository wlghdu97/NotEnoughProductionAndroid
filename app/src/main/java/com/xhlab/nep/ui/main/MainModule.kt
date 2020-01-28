package com.xhlab.nep.ui.main

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.main.items.ItemBrowserFragment
import com.xhlab.nep.ui.main.items.ItemBrowserModule
import com.xhlab.nep.ui.main.settings.SettingsFragment
import com.xhlab.nep.ui.main.settings.SettingsModule
import com.xhlab.nep.ui.parser.JsonParseDialog
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [ItemBrowserModule::class, SettingsModule::class])
@Suppress("unused")
abstract class MainModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun provideMainViewModel(viewModel: MainViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideItemBrowserFragment(): ItemBrowserFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideJsonParserDialog(): JsonParseDialog
}