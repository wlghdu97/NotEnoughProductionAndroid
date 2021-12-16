package com.xhlab.nep.shared.parser

import co.touchlab.stately.concurrency.AtomicInt
import co.touchlab.stately.concurrency.value
import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.model.form.recipes.MachineRecipeForm
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.parser.stream.JsonToken
import kotlinx.coroutines.flow.flow

@ProvideWithDagger("Parser")
class FurnaceRecipeParser constructor(
    private val itemParser: ItemParser,
    private val machineRepo: MachineRepo,
    private val recipeRepo: RecipeRepo
) : RecipeParser<MachineRecipeForm>() {

    private var machineId = AtomicInt(-1)

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

    override suspend fun parseElement(reader: JsonReader): MachineRecipeForm {
        var input: ItemForm? = null
        var output: ItemForm? = null

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

        return MachineRecipeForm(
            isEnabled = true,
            duration = 200,
            ept = 1,
            powerType = MachineRecipeView.Companion.PowerType.FUEL.type,
            machineId = machineId.value,
            itemInputs = listOf(input),
            itemOutputs = listOf(output),
            fluidInputs = emptyList(),
            fluidOutputs = emptyList()
        )
    }

    private suspend fun registerFurnace() {
        machineId.value = machineRepo.insertMachine("minecraft", "Furnace")
            ?: throw NullPointerException("failed to insert furnace.")
    }
}
