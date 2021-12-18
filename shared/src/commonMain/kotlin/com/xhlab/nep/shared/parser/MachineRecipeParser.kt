package com.xhlab.nep.shared.parser

import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.model.form.recipes.MachineRecipeForm
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.stream.JsonReader
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger

@ProvideWithDagger("Parser")
class MachineRecipeParser constructor(
    private val itemParser: ItemParser,
    private val fluidParser: FluidParser,
    private val machineRepo: MachineRepo,
    private val recipeRepo: RecipeRepo
) : RecipeParser<MachineRecipeForm>() {

    override suspend fun parse(type: String, reader: JsonReader) = flow {
        while (reader.hasNext()) {
            reader.nextName()
            parseMachineList(reader, type)
        }
    }

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

    private suspend fun FlowCollector<String>.parseMachine(
        reader: JsonReader,
        modName: String
    ) {
        reader.beginObject()

        var name = ""
        var recipes = emptyList<MachineRecipeForm>()

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

    override suspend fun parseElement(reader: JsonReader): MachineRecipeForm {
        var isEnabled = false
        var duration = 0
        var powerType = MachineRecipeView.Companion.PowerType.NONE
        var ept = 0
        var inputItems = emptyList<ItemForm>()
        var outputItems = emptyList<ItemForm>()
        var inputFluids = emptyList<FluidForm>()
        var outputFluids = emptyList<FluidForm>()

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
                    powerType = MachineRecipeView.Companion.PowerType.EU
                    ept = reader.nextInt()
                }
                "rft" -> {
                    powerType = MachineRecipeView.Companion.PowerType.RF
                    ept = reader.nextInt()
                }
            }
        }
        reader.endObject()

        return MachineRecipeForm(
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
