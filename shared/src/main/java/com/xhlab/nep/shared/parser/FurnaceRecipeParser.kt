package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.ItemParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class FurnaceRecipeParser @Inject constructor(
    private val itemParser: ItemParser,
    private val machineRepo: MachineRepo,
    private val recipeRepo: RecipeRepo
) : RecipeParser<MachineRecipe>() {

    private var machineId: Int = -1

    override suspend fun parse(type: String, reader: JsonReader) = CoroutineScope(coroutineContext).produce {
        send("parsing furnace recipes")
        registerFurnace()
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val recipeList = parseElements(reader)
                // insert recipes into db
                send("inserting ${recipeList.size} furnace recipes")
                recipeRepo.insertRecipes(recipeList)
            } else {
                reader.skipValue()
            }
        }
    }

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
