package com.xhlab.nep.shared.di

import android.content.Context
import com.xhlab.nep.shared.domain.icon.IconUnzipUseCase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.StringResolver
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers

@Module(
    includes = [
        ItemDomainModule::class,
        MachineDomainModule::class,
        ProcessDomainModule::class,
        ProcessEditorViewModelModule::class,
        RecipeDomainModule::class
    ]
)
@Suppress("unused")
class DomainModule {

    @Provides
    fun provideIconUnzipUseCase(
        context: Context,
        generalPreference: GeneralPreference,
        stringResolver: StringResolver
    ): IconUnzipUseCase {
        val outputDir = context.getExternalFilesDir("icons")
        return IconUnzipUseCase(generalPreference, stringResolver, outputDir!!, Dispatchers.IO)
    }
}
