package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.ShapedRecipe
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

internal class ShapedRecipeParser @Inject constructor(
    private val vanillaItemParser: VanillaItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapedRecipe>() {

    @ExperimentalCoroutinesApi
    override suspend fun parse(reader: JsonReader) = CoroutineScope(coroutineContext).produce {
        send("parsing shaped recipes")
        reader.nextName()
        val recipeList = parseElements(reader)
        // insert recipes into db
        recipeRepo.insertRecipes(recipeList)
    }

    override suspend fun parseElement(reader: JsonReader): ShapedRecipe {
        var inputItems = emptyList<Item?>()
        var outputItem: Item? = null

        reader.beginObject()
        while(reader.hasNext()) {
            when (reader.nextName()) {
                "iI" -> inputItems = vanillaItemParser.parseElements(reader)
                "o" -> outputItem = vanillaItemParser.parseElement(reader)
            }
        }
        reader.endObject()

        if (outputItem == null) {
            throw NullPointerException("output item is null.")
        }

        return ShapedRecipe(
            input = inputItems,
            output = outputItem
        )
    }
}