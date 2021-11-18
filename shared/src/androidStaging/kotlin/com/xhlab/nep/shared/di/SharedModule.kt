package com.xhlab.nep.shared.di

import android.app.Application
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module(
    includes = [
        SqlDelightModule::class,
        DomainModule::class,
        UIModule::class
    ]
)
@Suppress("unused")
class SharedModule {

    private val io: CoroutineDispatcher
        get() = Dispatchers.IO

    @Provides
    @Singleton
    fun provideStringResolver(app: Application) = StringResolver(app)

    @Provides
    @Singleton
    fun provideGeneralPreference(app: Application): GeneralPreference = GeneralSharedPreference(app)

    @Provides
    @Singleton
    internal fun provideElementRepo(db: Nep): ElementRepo = ElementRepoImpl(db, io)

    @Provides
    @Singleton
    internal fun provideReplacementAdder(db: Nep) = ReplacementAdder(db, io)

    @Provides
    @Singleton
    internal fun provideOreDictRepo(adder: ReplacementAdder): OreDictRepo = OreDictRepoImpl(adder)

    @Provides
    @Singleton
    internal fun provideMachineRepo(db: Nep): MachineRepo = MachineRepoImpl(db, io)

    @Provides
    @Singleton
    internal fun provideRecipeAdder(db: Nep) = RecipeAdder(db, io)

    @Provides
    @Singleton
    internal fun provideRecipeRepo(db: Nep, adder: RecipeAdder): RecipeRepo =
        RecipeRepoImpl(db, io, adder)

    @Provides
    @Singleton
    internal fun provideMachineRecipeRepo(db: Nep): MachineRecipeRepo =
        MachineRecipeRepoImpl(db, io)

    @Provides
    @Singleton
    internal fun provideProcessRepo(): ProcessRepo = FakeProcessRepo()
}
