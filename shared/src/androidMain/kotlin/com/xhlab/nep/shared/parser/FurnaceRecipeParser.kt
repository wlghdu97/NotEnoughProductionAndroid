package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.ItemParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FurnaceRecipeParser @Inject constructor(
    private val itemParser: ItemParser,
    private val machineRepo: MachineRepo,
    private val recipeRepo: RecipeRepo
) : RecipeParser<MachineRecipe>() {

    private var machineId: Int = -1

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(type: String, reader: JsonReader) = flow {
        emit("parsing furnace recipes")
        registerFurnace()
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                emit("inserting ${recipeList.size} furnace recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parseElement(reader: JsonReader): MachineRecipe {
        var input: Item? = null
        var output: Item? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "i" -> input = itemParser.parseElement(reader)
                "o" -> output = itemParser.parseElement(reader)
            }
        }
        reader.endObject()

        requireNotNull(input)
        requireNotNull(output)

        return MachineRecipe(
            isEnabled = true,
            duration = 200,
            ept = 1,
            powerType = MachineRecipe.Companion.PowerType.FUEL.type,
            machineId = machineId,
            itemInputs = listOf(input),
            itemOutputs = listOf(output),
            fluidInputs = emptyList(),
            fluidOutputs = emptyList()
        )
    }

    private suspend fun registerFurnace() {
        machineId = machineRepo.insertMachine("minecraft", "Furnace")
            ?: throw NullPointerException("failed to insert furnace.")
    }
}
