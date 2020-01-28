package com.xhlab.nep.di

import com.xhlab.nep.shared.di.SharedModule
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.ui.delegate.ThemeDelegate
import com.xhlab.nep.ui.delegate.ThemeDelegateImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [SharedModule::class])
class AppModule {
    @Provides
    @Singleton
    fun provideThemeDelegate(
        generalPreference: GeneralPreference
    ): ThemeDelegate = ThemeDelegateImpl(generalPreference)
}