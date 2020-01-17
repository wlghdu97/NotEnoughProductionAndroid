package com.xhlab.nep.shared.di

import android.app.Application
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.element.ElementRepoImpl
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.data.gregtech.GregtechRepoImpl
import com.xhlab.nep.shared.data.recipe.RecipeAdder
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepoImpl
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.preference.GeneralSharedPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [RoomModule::class])
class SharedModule {

    @Provides
    @Singleton
    fun provideGeneralPreference(app: Application): GeneralPreference = GeneralSharedPreference(app)

    @Provides
    @Singleton
    internal fun provideElementRepo(db: AppDatabase): ElementRepo = ElementRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideRecipeRepo(adder: RecipeAdder): RecipeRepo = RecipeRepoImpl(adder)

    @Provides
    @Singleton
    internal fun provideGregtechRepo(db: AppDatabase): GregtechRepo = GregtechRepoImpl(db)
}