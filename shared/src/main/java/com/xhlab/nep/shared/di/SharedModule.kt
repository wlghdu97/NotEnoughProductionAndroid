package com.xhlab.nep.shared.di

import android.app.Application
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.element.ElementRepoImpl
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.data.gregtech.GregtechRepoImpl
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepoImpl
import com.xhlab.nep.shared.db.AppDatabase
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import com.xhlab.nep.shared.preference.GeneralPreference
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ParserModule::class, RoomModule::class])
class SharedModule {

    @Provides
    @Singleton
    fun provideGeneralPreference(app: Application) = GeneralPreference(app)

    @Provides
    @Singleton
    internal fun provideElementRepo(db: AppDatabase): ElementRepo = ElementRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideRecipeRepo(db: AppDatabase): RecipeRepo = RecipeRepoImpl(db)

    @Provides
    @Singleton
    internal fun provideGregtechRepo(db: AppDatabase): GregtechRepo = GregtechRepoImpl(db)

    @Provides
    internal fun provideParseRecipeUseCase(
        gregtechRecipeParser: GregtechRecipeParser,
        shapedRecipeParser: ShapedRecipeParser,
        shapelessRecipeParser: ShapelessRecipeParser,
        elementRepo: ElementRepo,
        gregtechRepo: GregtechRepo,
        generalPreference: GeneralPreference
    ) = ParseRecipeUseCase(
        gregtechRecipeParser = gregtechRecipeParser,
        shapedRecipeParser = shapedRecipeParser,
        shapelessRecipeParser = shapelessRecipeParser,
        elementRepo = elementRepo,
        gregtechRepo = gregtechRepo,
        generalPreference = generalPreference
    )
}