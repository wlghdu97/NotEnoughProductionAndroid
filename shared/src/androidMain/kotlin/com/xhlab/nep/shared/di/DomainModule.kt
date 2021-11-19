package com.xhlab.nep.shared.di

import dagger.Module

@Module(
    includes = [
        ItemDomainModule::class,
        MachineDomainModule::class,
        ProcessDomainModule::class,
        ProcessEditorViewModelModule::class,
        RecipeDomainModule::class
    ]
)
@Suppress("unused")
class DomainModule
