package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ReplacementListModule {
    @Binds
    @IntoMap
    @ViewModelKey(ReplacementListViewModel::class)
    abstract fun provideReplacementListViewModel(viewModel: ReplacementListViewModel): ViewModel
}
