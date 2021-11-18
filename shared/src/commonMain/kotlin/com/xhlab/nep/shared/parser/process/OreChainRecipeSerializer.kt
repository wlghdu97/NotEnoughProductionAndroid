package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object OreChainRecipeSerializer : KSerializer<OreChainRecipe> {

    override val descriptor = OreChainRecipeSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: OreChainRecipe) {
        val inputElement = value.getInputs().first()
        val outputElement = value.getOutput().first()
        val surrogate = OreChainRecipeSurrogate(
            inputElement = inputElement,
            outputElement = outputElement
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
        val inputElement: RecipeElement,
        val outputElement: RecipeElement,
    )
}
