package com.xhlab.nep.shared.di

import com.xhlab.nep.shared.data.ElementRepo
import com.xhlab.nep.shared.data.GregtechRepo
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import dagger.Module
import dagger.Provides

@Module(includes = [ParserModule::class, RoomModule::class])
class SharedModule {
    @Provides
    internal fun provideParseRecipeUseCase(
        gregtechRecipeParser: GregtechRecipeParser,
        shapedRecipeParser: ShapedRecipeParser,
        shapelessRecipeParser: ShapelessRecipeParser,
        elementRepo: ElementRepo,
        gregtechRepo: GregtechRepo
    ) = ParseRecipeUseCase(
        gregtechRecipeParser = gregtechRecipeParser,
        shapedRecipeParser = shapedRecipeParser,
        shapelessRecipeParser = shapelessRecipeParser,
        elementRepo = elementRepo,
        gregtechRepo = gregtechRepo
    )
}