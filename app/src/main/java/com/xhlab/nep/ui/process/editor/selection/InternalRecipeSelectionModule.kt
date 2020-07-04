package com.xhlab.nep.ui.process.editor.selection

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class InternalRecipeSelectionModule {
    @Binds
    @IntoMap
    @ViewModelKey(InternalRecipeSelectionViewModel::class)
    abstract fun provideRecipeSelectionViewModel(viewModelInternal: InternalRecipeSelectionViewModel): ViewModel
}