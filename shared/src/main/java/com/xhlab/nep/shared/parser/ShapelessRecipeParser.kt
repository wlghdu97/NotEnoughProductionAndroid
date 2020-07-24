package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.ShapelessRecipe
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ShapelessRecipeParser @Inject constructor(
    private val vanillaItemParser: VanillaItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapelessRecipe>() {

    @ExperimentalCoroutinesApi
    override suspend fun parse(type: String, reader: JsonReader) = CoroutineScope(coroutineContext).produce {
        send("parsing shapeless recipes")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                send("inserting ${recipeList.size} shapeless recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

    override suspend fun parseElement(reader: JsonReader): ShapelessRecipe {
        var inputItems = emptyList<Item>()
        var outputItem: Item? = null

        reader.beginObject()
        while(reader.hasNext()) {
            when (reader.nextName()) {
                "iI" -> inputItems = vanillaItemParser.parseElements(reader).filterNotNull()
                "o" -> outputItem = vanillaItemParser.parseElement(reader)
            }
        }
        reader.endObject()

        if (outputItem == null) {
            throw NullPointerException("output item is null.")
        }

        return ShapelessRecipe(
            input = inputItems,
            output = outputItem
        )
    }
}