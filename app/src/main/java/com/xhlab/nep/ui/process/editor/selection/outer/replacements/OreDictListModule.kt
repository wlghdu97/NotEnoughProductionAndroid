package com.xhlab.nep.ui.process.editor.selection.outer.replacements

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.shared.ui.process.editor.selection.outer.replacements.OreDictListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class OreDictListModule {
    @Binds
    @IntoMap
    @ViewModelKey(OreDictListViewModel::class)
    abstract fun provideOreDictListViewModel(viewModel: OreDictListViewModel): ViewModel
}
