package com.xhlab.nep.ui.main.items

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.shared.ui.main.items.ItemBrowserViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ItemBrowserModule {
    @Binds
    @IntoMap
    @ViewModelKey(ItemBrowserViewModel::class)
    abstract fun provideItemBrowserViewModel(viewModel: ItemBrowserViewModel): ViewModel
}
