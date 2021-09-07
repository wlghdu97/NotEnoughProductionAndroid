package com.xhlab.nep.ui.process.calculator

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.ui.process.calculator.byproducts.ByproductsFragment
import com.xhlab.nep.ui.process.calculator.cycles.ProcessingOrderFragment
import com.xhlab.nep.ui.process.calculator.ingredients.BaseIngredientsFragment
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

    @Binds
    @IntoMap
    @ViewModelKey(ElementNavigatorViewModel::class)
    abstract fun provideBaseIngredientsViewModel(viewModel: ElementNavigatorViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideProcessingOrderFragment(): ProcessingOrderFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideBaseIngredientsFragment(): BaseIngredientsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideByproductsFragment(): ByproductsFragment
}
