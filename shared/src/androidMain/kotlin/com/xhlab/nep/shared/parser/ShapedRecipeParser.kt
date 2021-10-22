package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.ShapedRecipe
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShapedRecipeParser @Inject constructor(
    private val vanillaItemParser: VanillaItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapedRecipe>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(type: String, reader: JsonReader) = flow {
        emit("parsing shaped recipes")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                emit("inserting ${recipeList.size} shaped recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parseElement(reader: JsonReader): ShapedRecipe {
        var inputItems = emptyList<Item>()
        var outputItem: Item? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "iI" -> inputItems = vanillaItemParser.parseElements(reader).filterNotNull()
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
