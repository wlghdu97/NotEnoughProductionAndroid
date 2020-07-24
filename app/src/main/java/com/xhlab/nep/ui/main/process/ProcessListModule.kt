package com.xhlab.nep.ui.main.process

import androidx.lifecycle.ViewModel
import com.xhlab.nep.di.ViewModelKey
import com.xhlab.nep.di.scopes.ChildFragmentScope
import com.xhlab.nep.ui.main.process.creator.ProcessCreationDialog
import com.xhlab.nep.ui.main.process.creator.ProcessCreationModule
import com.xhlab.nep.ui.main.process.importer.ProcessImportDialog
import com.xhlab.nep.ui.main.process.importer.ProcessImportModule
import com.xhlab.nep.ui.main.process.rename.ProcessRenameDialog
import com.xhlab.nep.ui.main.process.rename.ProcessRenameModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
@Suppress("unused")
abstract class ProcessListModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProcessListViewModel::class)
    abstract fun provideProcessListViewModel(viewModel: ProcessListViewModel): ViewModel

    @ChildFragmentScope
    @ContributesAndroidInjector(modules = [ProcessCreationModule::class])
    abstract fun provideProcessCreationDialog(): ProcessCreationDialog

    @ChildFragmentScope
    @ContributesAndroidInjector(modules = [ProcessImportModule::class])
    abstract fun provideProcessImportDialog(): ProcessImportDialog

    @ChildFragmentScope
    @ContributesAndroidInjector(modules = [ProcessRenameModule::class])
    abstract fun provideProcessRenameDialog(): ProcessRenameDialog
}