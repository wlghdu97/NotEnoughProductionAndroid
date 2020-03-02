package com.xhlab.nep.di

import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailModule
import com.xhlab.nep.ui.main.MainActivity
import com.xhlab.nep.ui.main.MainModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ElementDetailModule::class])
    abstract fun provideElementDetailActivity(): ElementDetailActivity
}