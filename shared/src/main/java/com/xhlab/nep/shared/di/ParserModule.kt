package com.xhlab.nep.shared.di
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
        fluidParser: FluidParser
    ): GregtechRecipeParser =
        GregtechRecipeParser(itemParser, fluidParser)

    @Provides
    @Reusable
    internal fun provideShapedRecipeParser(
        vanillaItemParser: VanillaItemParser
    ): ShapedRecipeParser =
        ShapedRecipeParser(vanillaItemParser)

    @Provides
    @Reusable
    internal fun provideShapelessRecipeParser(
        vanillaItemParser: VanillaItemParser
    ): ShapelessRecipeParser =
        ShapelessRecipeParser(vanillaItemParser)
}