package com.xhlab.nep.di

import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailModule
import com.xhlab.nep.ui.main.MainActivity
import com.xhlab.nep.ui.main.MainModule
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity
import com.xhlab.nep.ui.main.machines.details.MachineResultModule
import com.xhlab.nep.ui.main.process.editor.ProcessEditActivity
import com.xhlab.nep.ui.main.process.editor.ProcessEditModule
import com.xhlab.nep.ui.process.calculator.ProcessCalculationActivity
import com.xhlab.nep.ui.process.calculator.ProcessCalculationModule
import com.xhlab.nep.ui.recipe.MachineRecipeListActivity
import com.xhlab.nep.ui.recipe.MachineRecipeListModule
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

    @ContributesAndroidInjector(modules = [MachineRecipeListModule::class])
    abstract fun provideMachineRecipeListActivity(): MachineRecipeListActivity

    @ContributesAndroidInjector(modules = [ProcessEditModule::class])
    abstract fun provideProcessEditActivity(): ProcessEditActivity

    @ContributesAndroidInjector(modules = [ProcessCalculationModule::class])
    abstract fun provideProcessCalculationActivity(): ProcessCalculationActivity
}