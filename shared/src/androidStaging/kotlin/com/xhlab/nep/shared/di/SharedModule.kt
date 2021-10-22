package com.xhlab.nep.shared.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xhlab.nep.shared.data.FakeProcessRepo
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.element.ElementRepoImpl
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.machine.MachineRepoImpl
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepoImpl
import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.data.oredict.OreDictRepoImpl
import com.xhlab.nep.shared.data.oredict.ReplacementAdder
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.data.recipe.RecipeAdder
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepoImpl
import com.xhlab.nep.shared.db.Nep
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.preference.GeneralSharedPreference
import com.xhlab.nep.shared.util.StringResolver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [SqlDelightModule::class])
@Suppress("unused")
class SharedModule {
    @Provides
    @Singleton
    fun provideStringResolver(app: Application) = StringResolver(app)
    
    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideGeneralPreference(app: Application): GeneralPreference = GeneralSharedPreference(app)

    @Provides
    @Singleton
    internal fun provideElementRepo(db: Nep): ElementRepo = ElementRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideReplacementAdder(db: Nep) = ReplacementAdder(db)

    @Provides
    @Singleton
    internal fun provideOreDictRepo(adder: ReplacementAdder): OreDictRepo = OreDictRepoImpl(adder)

    @Provides
    @Singleton
    internal fun provideMachineRepo(db: Nep): MachineRepo = MachineRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideRecipeAdder(db: Nep) = RecipeAdder(db)

    @Provides
    @Singleton
    internal fun provideRecipeRepo(db: Nep, adder: RecipeAdder): RecipeRepo =
        RecipeRepoImpl(db, adder)

    @Provides
    @Singleton
    internal fun provideMachineRecipeRepo(db: Nep): MachineRecipeRepo =
        MachineRecipeRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideProcessRepo(): ProcessRepo = FakeProcessRepo()
}
