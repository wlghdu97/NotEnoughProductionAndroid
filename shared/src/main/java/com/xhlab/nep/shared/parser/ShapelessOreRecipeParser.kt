package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.recipes.ShapelessOreDictRecipe
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.oredict.OreDictItemParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ShapelessOreRecipeParser @Inject constructor(
    private val oreDictItemParser: OreDictItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapelessOreDictRecipe>() {

    @ExperimentalCoroutinesApi
    override suspend fun parse(reader: JsonReader) = CoroutineScope(coroutineContext).produce {
        send("parsing shapeless ore recipes")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                send("inserting ${recipeList.size} shapeless ore recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

    override suspend fun parseElement(reader: JsonReader): ShapelessOreDictRecipe {
        var inputItems = emptyList<Element>()
        var outputItem: Element? = null

        reader.beginObject()
        while(reader.hasNext()) {
            when (reader.nextName()) {
                "iI" -> inputItems = oreDictItemParser.parseElements(reader)
                "o" -> outputItem = oreDictItemParser.parseElement(reader)
            }
        }
        reader.endObject()

        if (outputItem == null) {
            throw NullPointerException("output item is null.")
        }

        return ShapelessOreDictRecipe(
            input = inputItems,
            output = outputItem
        )
    }
}