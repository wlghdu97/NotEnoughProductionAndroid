package com.xhlab.nep.ui.process.editor.selection.outer

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListModule
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class RecipeSelectionModule {
    @Binds
    @IntoMap
    @ViewModelKey(RecipeSelectionViewModel::class)
    abstract fun provideRecipeSelectionViewModel(viewModel: RecipeSelectionViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector(modules = [RecipeListModule::class])
    abstract fun provideRecipeListFragment(): RecipeListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MachineRecipeListModule::class])
    abstract fun provideMachineRecipeListFragment(): MachineRecipeListFragment
}