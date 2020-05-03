package com.xhlab.nep.di

import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailModule
import com.xhlab.nep.ui.main.MainActivity
import com.xhlab.nep.ui.main.MainModule
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity
import com.xhlab.nep.ui.main.machines.details.MachineResultModule
import com.xhlab.nep.ui.recipe.StationRecipeListActivity
import com.xhlab.nep.ui.recipe.StationRecipeListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
@Suppress("unused")
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ElementDetailModule::class])
    abstract fun provideElementDetailActivity(): ElementDetailActivity

    @ContributesAndroidInjector(modules = [MachineResultModule::class])
    abstract fun bindMachineResultActivity(): MachineResultActivity

    @ContributesAndroidInjector(modules = [StationRecipeListModule::class])
    abstract fun provideStationRecipeListActivity(): StationRecipeListActivity
}