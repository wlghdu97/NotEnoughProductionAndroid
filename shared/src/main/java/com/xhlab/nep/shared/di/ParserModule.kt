package com.xhlab.nep.shared.di

import com.xhlab.nep.shared.data.RecipeRepo
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class ParserModule {
    @Provides
    @Reusable
    internal fun provideVanillaItemParser(): VanillaItemParser =
        VanillaItemParser()

    @Provides
    @Reusable
    internal fun provideItemParser(): ItemParser =
        ItemParser()

    @Provides
    @Reusable
    internal fun provideFluidParser(): FluidParser =
        FluidParser()

    @Provides
    @Reusable
    internal fun provideGregtechRecipeParser(
        itemParser: ItemParser,
        fluidParser: FluidParser,
        recipeRepo: RecipeRepo
    ): GregtechRecipeParser =
        GregtechRecipeParser(itemParser, fluidParser, recipeRepo)

    @Provides
    @Reusable
    internal fun provideShapedRecipeParser(
        vanillaItemParser: VanillaItemParser,
        recipeRepo: RecipeRepo
    ): ShapedRecipeParser =
        ShapedRecipeParser(vanillaItemParser, recipeRepo)

    @Provides
    @Reusable
    internal fun provideShapelessRecipeParser(
        vanillaItemParser: VanillaItemParser,
        recipeRepo: RecipeRepo
    ): ShapelessRecipeParser =
        ShapelessRecipeParser(vanillaItemParser, recipeRepo)
}