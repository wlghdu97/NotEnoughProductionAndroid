package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SupplierRecipeSerializer : KSerializer<SupplierRecipe> {

    override val descriptor = SupplierRecipeSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: SupplierRecipe) {
        val element = value.getOutput().first()
        val surrogate = SupplierRecipeSurrogate(element)
        encoder.encodeSerializableValue(SupplierRecipeSurrogate.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): SupplierRecipe {
        val surrogate = decoder.decodeSerializableValue(SupplierRecipeSurrogate.serializer())
        return SupplierRecipe(surrogate.innerElement)
    }

    @Serializable
    data class SupplierRecipeSurrogate(val innerElement: RecipeElement)
}
