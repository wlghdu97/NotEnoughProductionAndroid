package com.xhlab.nep.di

import com.xhlab.nep.di.scopes.ServiceScope
import com.xhlab.nep.service.IconUnzipService
import com.xhlab.nep.service.ParseRecipeService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {
    @ServiceScope
    @ContributesAndroidInjector
    abstract fun provideParseRecipeService(): ParseRecipeService

    @ServiceScope
    @ContributesAndroidInjector
    abstract fun provideIconUnzipService(): IconUnzipService
}