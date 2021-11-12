package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.RecipeElement
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ProcessElementSerializer : KSerializer<RecipeElement> {

    override val descriptor = RecipeElementImpl.serializer().descriptor

    override fun serialize(encoder: Encoder, value: RecipeElement) {
        val surrogate = with(value) {
            RecipeElementImpl(
                id = id,
                localizedName = localizedName,
                unlocalizedName = unlocalizedName,
                type = type,
                metaData = metaData,
                amount = amount
            )
        }
        encoder.encodeSerializableValue(RecipeElementImpl.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): RecipeElement {
        val element = decoder.decodeSerializableValue(RecipeElementImpl.serializer())
        return with(element) {
            RecipeElementImpl(
                id = id,
                localizedName = localizedName,
                unlocalizedName = unlocalizedName,
                type = type,
                metaData = metaData,
                amount = amount
            )
        }
    }

    @Serializable
    data class RecipeElementImpl(
        override val id: Long,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val amount: Int,
        override val type: Int,
        override val metaData: String? = null
    ) : RecipeElement()

    internal fun RecipeElement.toRecipeElementImpl() = RecipeElementImpl(
        id = id,
        localizedName = localizedName,
        unlocalizedName = unlocalizedName,
        type = type,
        metaData = metaData,
        amount = amount
    )
}
