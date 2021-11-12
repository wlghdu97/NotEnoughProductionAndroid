package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.shared.parser.process.ProcessElementSerializer.RecipeElementImpl
import com.xhlab.nep.shared.parser.process.ProcessElementSerializer.toRecipeElementImpl
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object ProcessRecipeSerializer : JsonContentPolymorphicSerializer<Recipe>(Recipe::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out Recipe> {
        return when {
            "machineName" in element.jsonObject -> MachineRecipeViewImpl.serializer()
            "innerElement" in element.jsonObject -> SupplierRecipeSerializer()
            "inputElement" in element.jsonObject -> OreChainRecipeSerializer()
            else -> CraftingRecipeViewImpl.serializer()
        }
    }

    class SupplierRecipeSerializer : KSerializer<SupplierRecipe> {

        override val descriptor = SupplierRecipeSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: SupplierRecipe) {
            val element = value.getOutput().first()
            val surrogate = SupplierRecipeSurrogate(element.toRecipeElementImpl())
            encoder.encodeSerializableValue(SupplierRecipeSurrogate.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): SupplierRecipe {
            val surrogate = decoder.decodeSerializableValue(SupplierRecipeSurrogate.serializer())
            return SupplierRecipe(surrogate.innerElement)
        }

        @Serializable
        data class SupplierRecipeSurrogate(val innerElement: RecipeElementImpl)
    }

    class OreChainRecipeSerializer : KSerializer<OreChainRecipe> {

        override val descriptor = OreChainRecipeSurrogate.serializer().descriptor

        override fun serialize(encoder: Encoder, value: OreChainRecipe) {
            val inputElement = value.getInputs().first()
            val outputElement = value.getOutput().first()
            val surrogate = OreChainRecipeSurrogate(
                inputElement = inputElement.toRecipeElementImpl(),
                outputElement = outputElement.toRecipeElementImpl()
            )
            encoder.encodeSerializableValue(OreChainRecipeSurrogate.serializer(), surrogate)
        }

        override fun deserialize(decoder: Decoder): OreChainRecipe {
            val surrogate = decoder.decodeSerializableValue(OreChainRecipeSurrogate.serializer())
            return OreChainRecipe(
                ingredient = surrogate.inputElement,
                oreDictElement = surrogate.outputElement
            )
        }

        @Serializable
        data class OreChainRecipeSurrogate(
            val inputElement: RecipeElementImpl,
            val outputElement: RecipeElementImpl,
        )
    }

    @Serializable
    data class MachineRecipeViewImpl(
        override val recipeId: Long,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String,
        override val itemList: List<RecipeElementImpl>,
        override val resultItemList: List<RecipeElementImpl>
    ) : MachineRecipeView()

    @Serializable
    data class CraftingRecipeViewImpl(
        override val recipeId: Long,
        override val itemList: List<RecipeElementImpl>,
        override val resultItemList: List<RecipeElementImpl>
    ) : CraftingRecipeView()
}
