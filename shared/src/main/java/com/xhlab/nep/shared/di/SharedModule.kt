package com.xhlab.nep.shared.di

import android.app.Application
import com.xhlab.nep.shared.data.ElementRepo
import com.xhlab.nep.shared.data.GregtechRepo
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