package com.xhlab.nep.shared.parser

import android.util.Log
import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.ShapelessRecipe
import com.xhlab.nep.shared.data.RecipeRepo
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import javax.inject.Inject

internal class ShapelessRecipeParser @Inject constructor(
    private val vanillaItemParser: VanillaItemParser,
    private val recipeRepo: RecipeRepo
) : RecipeParser<ShapelessRecipe>() {

    override suspend fun parse(reader: JsonReader) {
        Log.i(TAG, "start parsing, ${reader.nextName()}")
        val recipeList = parseElements(reader)
        // insert recipes into db
        recipeRepo.insertRecipes(recipeList)
    }

    override suspend fun parseElement(reader: JsonReader): ShapelessRecipe {
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

        return ShapelessRecipe(
            input = inputItems,
            output = outputItem
        )
    }

    companion object {
        private const val TAG = "shapeless_recipe_parser"
    }
}