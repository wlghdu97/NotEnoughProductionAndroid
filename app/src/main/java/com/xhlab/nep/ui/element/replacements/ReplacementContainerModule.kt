package com.xhlab.nep.ui.element.replacements

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class ReplacementContainerModule {
    @Binds
    @IntoMap
    @ViewModelKey(OreDictListViewModel::class)
    abstract fun bindOreDictListViewModel(viewModel: OreDictListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReplacementListViewModel::class)
    abstract fun bindReplacementListViewModel(viewModel: ReplacementListViewModel): ViewModel

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideOreDictListFragment(): OreDictListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun provideReplacementListFragment(): ReplacementListFragment
}
