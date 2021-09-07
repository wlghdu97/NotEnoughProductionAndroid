package com.xhlab.nep.ui.element.usages

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class UsageListModule {
    @Binds
    @IntoMap
    @ViewModelKey(UsageListViewModel::class)
    abstract fun bindUsageListViewModel(viewMOdel: UsageListViewModel): ViewModel
}
