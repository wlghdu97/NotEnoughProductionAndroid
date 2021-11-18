package com.xhlab.nep.shared.di

import dagger.Module

@Module(
    includes = [
        ViewModelModule::class,
        ProcessViewModelModule::class
    ]
)
@Suppress("unused")
class UIModule
