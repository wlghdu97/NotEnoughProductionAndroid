package com.xhlab.nep.shared.parser.element

import com.xhlab.nep.MR
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.shared.parser.Parser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.parser.stream.JsonToken
import com.xhlab.nep.shared.util.StringResolver
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger

@ProvideWithDagger("Parser")
class ItemParser constructor(
    private val stringResolver: StringResolver
) : Parser<ItemForm> {

    override suspend fun parseElement(reader: JsonReader): ItemForm {
        var amount = 0
        var unlocalizedName = ""
        var localizedName = ""
        var metaData: String? = null

        reader.beginObject()
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.NULL) {
                reader.skipValue()
                continue
            }
            when (reader.nextName()) {
                "a" -> amount = reader.nextInt()
                "uN" -> {
                    if (reader.peek() != JsonToken.NULL) {
                        unlocalizedName = reader.nextString()
                    }
                }
                "lN" -> {
                    if (reader.peek() != JsonToken.NULL) {
                        localizedName = reader.nextString()
                    }
                }
                "cfg" ->
                    metaData = reader.nextInt().toString() // circuit configuration
                "meta" ->
                    metaData = reader.nextString() // meta data
                "percentage" ->
                    metaData = stringResolver.formatString(
                        MR.strings.form_drop_chance,
                        reader.nextDouble() * 100
                    ) // drop chance
            }
        }
        reader.endObject()

        return ItemForm(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName,
            metaData = metaData
        )
    }
}
