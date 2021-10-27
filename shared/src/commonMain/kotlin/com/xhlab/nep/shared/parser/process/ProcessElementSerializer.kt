package com.xhlab.nep.shared.parser.process

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.recipes.view.RecipeElementView
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ProcessElementSerializer : KSerializer<Element> {

    override val descriptor = RecipeElementViewImpl.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Element) {
        val surrogate = with(value as ElementView) {
            RecipeElementViewImpl(
                id = id,
                localizedName = localizedName,
                unlocalizedName = unlocalizedName,
                type = type,
                metaData = metaData,
                amount = amount
            )
        }
        encoder.encodeSerializableValue(RecipeElementViewImpl.serializer(), surrogate)
    }

    override fun deserialize(decoder: Decoder): Element {
        val element = decoder.decodeSerializableValue(RecipeElementViewImpl.serializer())
        return with(element) {
            RecipeElementViewImpl(
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
    data class RecipeElementViewImpl(
        override val id: Long,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val amount: Int,
        override val type: Int,
        override val metaData: String? = null
    ) : RecipeElementView()

    internal fun ElementView.toRecipeElementViewImpl() = RecipeElementViewImpl(
        id = id,
        localizedName = localizedName,
        unlocalizedName = unlocalizedName,
        type = type,
        metaData = metaData,
        amount = amount
    )
}
