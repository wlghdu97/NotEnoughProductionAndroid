package com.xhlab.nep.ui.main

import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.parser.JsonParseDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class MainModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideJsonParserDialog(): JsonParseDialog
}