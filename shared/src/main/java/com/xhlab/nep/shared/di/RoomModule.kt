package com.xhlab.nep.shared.di

import android.app.Application
import androidx.room.Room
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.db.ProcessDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase = Room.databaseBuilder(
        app.applicationContext, AppDatabase::class.java, "nep.db"
    ).build()

    @Provides
    @Singleton
    fun provideProcessDatabase(app: Application): ProcessDatabase = Room.databaseBuilder(
        app.applicationContext, ProcessDatabase::class.java, "process.db"
    ).build()
}
