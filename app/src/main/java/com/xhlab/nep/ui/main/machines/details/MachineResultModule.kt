package com.xhlab.nep.ui.main.machines.details

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import com.xhlab.nep.shared.ui.main.machines.details.MachineResultViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class MachineResultModule {
    @Binds
    @IntoMap
    @ViewModelKey(MachineResultViewModel::class)
    abstract fun provideMachineResultViewModel(viewModel: MachineResultViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideMachineResultFragment(): MachineResultFragment
}
