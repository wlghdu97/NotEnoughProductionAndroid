package com.xhlab.nep.di

import com.xhlab.nep.ui.element.ElementDetailActivity
import com.xhlab.nep.ui.element.ElementDetailModule
import com.xhlab.nep.ui.main.MainActivity
import com.xhlab.nep.ui.main.MainModule
import com.xhlab.nep.ui.main.machines.details.MachineResultActivity
import com.xhlab.nep.ui.main.machines.details.MachineResultModule
import com.xhlab.nep.ui.main.process.creator.browser.ItemBrowserActivity
import com.xhlab.nep.ui.main.process.creator.browser.ItemBrowserModule
import com.xhlab.nep.ui.process.calculator.ProcessCalculationActivity
import com.xhlab.nep.ui.process.calculator.ProcessCalculationModule
import com.xhlab.nep.ui.process.editor.ProcessEditActivity
import com.xhlab.nep.ui.process.editor.ProcessEditModule
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.internal.InternalRecipeSelectionModule
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.outer.RecipeSelectionModule
import com.xhlab.nep.ui.process.editor.selection.subprocess.ProcessSelectionActivity
import com.xhlab.nep.ui.process.editor.selection.subprocess.ProcessSelectionModule
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

    @ContributesAndroidInjector(modules = [ItemBrowserModule::class])
    abstract fun provideItemBrowserActivity(): ItemBrowserActivity

    @ContributesAndroidInjector(modules = [ProcessEditModule::class])
    abstract fun provideProcessEditActivity(): ProcessEditActivity

    @ContributesAndroidInjector(modules = [InternalRecipeSelectionModule::class])
    abstract fun provideInternalRecipeSelectionActivity(): InternalRecipeSelectionActivity

    @ContributesAndroidInjector(modules = [RecipeSelectionModule::class])
    abstract fun provideRecipeSelectionActivity(): RecipeSelectionActivity

    @ContributesAndroidInjector(modules = [ProcessSelectionModule::class])
    abstract fun provideProcessSelectionActivity(): ProcessSelectionActivity

    @ContributesAndroidInjector(modules = [ProcessCalculationModule::class])
    abstract fun provideProcessCalculationActivity(): ProcessCalculationActivity
}
