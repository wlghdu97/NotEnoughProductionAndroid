package com.xhlab.nep.ui.process.editor.selection.outer

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.shared.ui.process.editor.selection.outer.RecipeSelectionViewModel
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.details.MachineRecipeListModule
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.recipes.RecipeListModule
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.OreDictListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.OreDictListModule
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.ReplacementListFragment
import com.xhlab.nep.ui.process.editor.selection.outer.replacements.ReplacementListModule
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
    @ContributesAndroidInjector(modules = [OreDictListModule::class])
    abstract fun provideOreDictListFragment(): OreDictListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ReplacementListModule::class])
    abstract fun provideReplacementListFragment(): ReplacementListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [MachineRecipeListModule::class])
    abstract fun provideMachineRecipeListFragment(): MachineRecipeListFragment
}
