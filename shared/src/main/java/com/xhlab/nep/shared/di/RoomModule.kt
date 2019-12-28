package com.xhlab.nep.shared.di

import android.app.Application
import androidx.room.Room
import com.xhlab.nep.shared.db.AppDatabase
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
}