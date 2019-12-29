package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.GregtechRecipe
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import timber.log.Timber
import javax.inject.Inject

internal class GregtechRecipeParser @Inject constructor(
    private val itemParser: ItemParser,
    private val fluidParser: FluidParser,
    private val recipeRepo: RecipeRepo,
    private val gregtechRepo: GregtechRepo
) : RecipeParser<GregtechRecipe>() {

    override suspend fun parse(reader: JsonReader) {
        parseMachineList(reader)
    }

    private suspend fun parseMachineList(reader: JsonReader) {
        reader.beginArray()
        while (reader.hasNext()) {
            parseMachine(reader)
        }
        reader.endArray()
    }

    private suspend fun parseMachine(reader: JsonReader) {
        reader.beginObject()

        var name = ""
        var recipes = emptyList<GregtechRecipe>()

        while (reader.hasNext()) {
            if (reader.nextName() == "n") {
                name = reader.nextString()
            } else {
                recipes = parseElements(reader)
            }
        }
        Timber.i("machine ${recipes.size} : $name")
        reader.endObject()

        // map machine name to recipes
        val machineId = gregtechRepo.insertGregtechMachine(name)
        val mappedRecipes = recipes.map {
            it.copy(machineId = machineId)
        }

        // insert recipes into db
        recipeRepo.insertRecipes(mappedRecipes)
    }

    override suspend fun parseElement(reader: JsonReader): GregtechRecipe {
        var isEnabled = false
        var duration = 0
        var eut = 0
        var inputItems = emptyList<Item>()
        var outputItems = emptyList<Item>()
        var inputFluids = emptyList<Fluid>()
        var outputFluids = emptyList<Fluid>()

        reader.beginObject()

        while (reader.hasNext()) {
            when (reader.nextName()) {
                "en" -> isEnabled = reader.nextBoolean()
                "dur" -> duration = reader.nextInt()
                "eut" -> eut = reader.nextInt()
                "iI" -> inputItems = itemParser.parseElements(reader)
                "iO" -> outputItems = itemParser.parseElements(reader)
                "fI" -> inputFluids = fluidParser.parseElements(reader)
                "fO" -> outputFluids = fluidParser.parseElements(reader)
            }
        }
        reader.endObject()

        return  GregtechRecipe(
            isEnabled = isEnabled,
            duration = duration,
            eut = eut,
            machineId = -1,
            itemInputs = inputItems,
            itemOutputs = outputItems,
            fluidInputs = inputFluids,
            fluidOutputs = outputFluids
        )
    }
}