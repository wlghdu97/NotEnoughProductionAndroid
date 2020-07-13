package com.xhlab.nep.ui.process.calculator.ingredients

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class BaseIngredientsModule {
    @Binds
    @IntoMap
    @ViewModelKey(BaseIngredientsViewModel::class)
    abstract fun provideBaseIngredientsViewModel(viewModel: BaseIngredientsViewModel): ViewModel
}