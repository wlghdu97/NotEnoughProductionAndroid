package com.xhlab.nep.shared.parser.process

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.recipes.ProcessRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeElementView
import java.lang.reflect.Type

class ProcessDeserializer : JsonDeserializer<Process> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Process {
        val jsonObject = json.asJsonObject
        val process = Process(
            id = jsonObject.get("id").asString,
            name = jsonObject.get("name").asString,
            rootRecipe = jsonObject.get("rootRecipe").asJsonObject.toRecipe(),
            targetOutput = jsonObject.get("targetOutput").asJsonObject.toElement()
        )
        val vertices = jsonObject.get("vertices").asJsonArray.map { it.asJsonObject.toRecipe() }
        val edges = jsonObject.get("edges").asJsonArray.map { it.asJsonObject.toEdge() }
        for (edge in edges) {
            if (edge.from != edge.to) {
                val from = vertices[edge.to]
                val to = vertices[edge.from]
                val element = from.getElement(edge.key)
                if (element != null) {
                    process.connectRecipe(from, to, element, edge.reversed)
                } else {
                    throw NullPointerException()
                }
            } else {
                val recipe = vertices[edge.from]
                val element = recipe.getElement(edge.key)
                if (element != null) {
                    process.markNotConsumed(recipe, element, false)
                } else {
                    throw NullPointerException()
                }
            }
        }
        return process
    }

    private fun Recipe.getElement(key: String): Element? {
        return (getInputs() + getOutput()).find { it.unlocalizedName == key }
    }

    private fun JsonObject.toRecipe(): Recipe {
        return when {
            get("machineName") != null -> {
                MachineRecipeViewImpl(
                    recipeId = get("recipeId").asLong,
                    isEnabled = get("isEnabled").asBoolean,
                    duration = get("duration").asInt,
                    powerType = get("powerType").asInt,
                    ept = get("ept").asInt,
                    machineId = get("machineId").asInt,
                    machineName = get("machineName").asString,
                    itemList = get("itemList").asJsonArray.map { it.asJsonObject.toElement() },
                    resultItemList = get("resultItemList").asJsonArray.map { it.asJsonObject.toElement() }
                )
            }
            get("processElement") != null -> {
                val processElement = get("processElement").asJsonObject
                ProcessRecipe(
                    element = get("innerElement").asJsonObject.toElement(),
                    processName = processElement.get("localizedName").asString,
                    processId = processElement.get("unlocalizedName").asString
                )
            }
            get("innerElement") != null -> {
                SupplierRecipe(get("innerElement").asJsonObject.toElement())
            }
            get("inputElement") != null -> {
                OreChainRecipe(
                    ingredient = get("inputElement").asJsonObject.toElement(),
                    oreDictElement = get("outputElement").asJsonObject.toElement()
                )
            }
            else -> {
                CraftingRecipeViewImpl(
                    recipeId = get("recipeId").asLong,
                    itemList = get("itemList").asJsonArray.map { it.asJsonObject.toElement() },
                    resultItemList = get("resultItemList").asJsonArray.map { it.asJsonObject.toElement() }
                )
            }
        }
    }

    private fun JsonObject.toElement() = RecipeElementViewImpl(
        id = get("id").asLong,
        unlocalizedName = get("unlocalizedName").asString,
        localizedName = get("localizedName").asString,
        amount = get("amount").asInt,
        type = get("type").asInt,
        metaData = get("metaData")?.asString
    )

    private fun JsonObject.toEdge() = Edge(
        from = get("from").asInt,
        to = get("to").asInt,
        key = get("key").asString,
        reversed = get("reversed").asBoolean
    )

    private data class MachineRecipeViewImpl(
        override val recipeId: Long,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String,
        override val itemList: List<RecipeElementViewImpl>,
        override val resultItemList: List<RecipeElementViewImpl>
    ) : MachineRecipeView()

    private data class CraftingRecipeViewImpl(
        override val recipeId: Long,
        override val itemList: List<RecipeElementView>,
        override val resultItemList: List<RecipeElementView>
    ) : CraftingRecipeView()

    private data class RecipeElementViewImpl(
        override val id: Long,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val amount: Int,
        override val type: Int,
        override val metaData: String?
    ) : RecipeElementView()

    private data class Edge(
        val from: Int,
        val to: Int,
        val key: String,
        val reversed: Boolean
    )
}