package com.xhlab.nep.ui.process.calculator

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.process.calculator.ingredients.BaseIngredientsFragment
import com.xhlab.nep.ui.process.calculator.ingredients.BaseIngredientsModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessCalculationModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessCalculationViewModel::class)
    abstract fun provideProcessCalculationViewModel(viewModel: ProcessCalculationViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector(modules = [BaseIngredientsModule::class])
    abstract fun provideBaseIngredientsFragment(): BaseIngredientsFragment
}