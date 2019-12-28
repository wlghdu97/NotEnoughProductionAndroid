package com.xhlab.nep.shared.parser

import android.util.Log
import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.ShapedRecipe
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import javax.inject.Inject

internal class ShapedRecipeParser @Inject constructor(
    private val vanillaItemParser: VanillaItemParser
) : RecipeParser<ShapedRecipe>() {

    override suspend fun parse(reader: JsonReader) {
        Log.i(TAG, "start parsing, ${reader.nextName()}")
        parseElements(reader)
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

    companion object {
        private const val TAG = "shaped_recipe_parser"
    }
}