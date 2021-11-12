package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.recipes.ShapedOreDictRecipeForm
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.oredict.OreDictItemParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShapedOreRecipeParser @Inject constructor(
    private val oreDictItemParser: OreDictItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapedOreDictRecipeForm>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(type: String, reader: JsonReader) = flow {
        emit("parsing shaped ore recipes")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                emit("inserting ${recipeList.size} shaped ore recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parseElement(reader: JsonReader): ShapedOreDictRecipeForm {
        var inputItems = emptyList<ElementForm>()
        var outputItem: ElementForm? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "iI" -> inputItems = oreDictItemParser.parseElements(reader)
                "o" -> outputItem = oreDictItemParser.parseElement(reader)
            }
        }
        reader.endObject()

        if (outputItem == null) {
            throw NullPointerException("output item is null.")
        }

        return ShapedOreDictRecipeForm(
            input = inputItems,
            output = outputItem
        )
    }
}
