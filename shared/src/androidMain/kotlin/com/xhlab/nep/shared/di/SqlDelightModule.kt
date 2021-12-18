package com.xhlab.nep.shared.di

import android.app.Application
import com.xhlab.nep.shared.db.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SqlDelightModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): Nep {
        return NepDriverFactory(app).createDatabase()
    }

    @Provides
    @Singleton
    fun provideProcessDatabase(app: Application): NepProcess {
        return ProcessDriverFactory(app).createDatabase()
    }
}
