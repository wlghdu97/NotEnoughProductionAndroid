package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MachineRecipeParser @Inject constructor(
    private val itemParser: ItemParser,
    private val fluidParser: FluidParser,
    private val machineRepo: MachineRepo,
    private val recipeRepo: RecipeRepo
) : RecipeParser<MachineRecipe>() {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parse(type: String, reader: JsonReader) = flow {
        while (reader.hasNext()) {
            reader.nextName()
            parseMachineList(reader, type)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun FlowCollector<String>.parseMachineList(
        reader: JsonReader,
        modName: String
    ) {
        reader.beginArray()
        while (reader.hasNext()) {
            parseMachine(reader, modName)
        }
        reader.endArray()
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun FlowCollector<String>.parseMachine(
        reader: JsonReader,
        modName: String
    ) {
        reader.beginObject()

        var name = ""
        var recipes = emptyList<MachineRecipe>()

        while (reader.hasNext()) {
            if (reader.nextName() == "n") {
                name = reader.nextString()
            } else {
                recipes = parseElements(reader)
            }
        }

        emit("$name (${recipes.size} recipes)")
        reader.endObject()

        // map machine name to recipes
        val machineId = machineRepo.insertMachine(modName, name)
            ?: throw NullPointerException("machine id is null.")
        val mappedRecipes = recipes.map {
            it.copy(machineId = machineId)
        }

        // insert recipes into db
        recipeRepo.insertRecipes(mappedRecipes)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun parseElement(reader: JsonReader): MachineRecipe {
        var isEnabled = false
        var duration = 0
        var powerType = PowerType.NONE
        var ept = 0
        var inputItems = emptyList<Item>()
        var outputItems = emptyList<Item>()
        var inputFluids = emptyList<Fluid>()
        var outputFluids = emptyList<Fluid>()

        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "en" -> isEnabled = reader.nextBoolean()
                "dur" -> duration = reader.nextInt()
                "iI" -> inputItems = itemParser.parseElements(reader)
                "iO" -> outputItems = itemParser.parseElements(reader)
                "fI" -> inputFluids = fluidParser.parseElements(reader)
                "fO" -> outputFluids = fluidParser.parseElements(reader)
                "eut" -> {
                    powerType = PowerType.EU
                    ept = reader.nextInt()
                }
                "rft" -> {
                    powerType = PowerType.RF
                    ept = reader.nextInt()
                }
            }
        }
        reader.endObject()

        return MachineRecipe(
            isEnabled = isEnabled,
            duration = duration,
            powerType = powerType.type,
            ept = ept,
            machineId = -1,
            itemInputs = inputItems,
            itemOutputs = outputItems,
            fluidInputs = inputFluids,
            fluidOutputs = outputFluids
        )
    }
}
