package com.xhlab.nep.ui.element

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ElementDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(ElementDetailViewModel::class)
    abstract fun bindElementDetailViewModel(viewModel: ElementDetailViewModel): ViewModel
}