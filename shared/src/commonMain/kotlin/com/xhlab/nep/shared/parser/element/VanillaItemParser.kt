package com.xhlab.nep.shared.parser.element

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.shared.parser.Parser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.parser.stream.JsonToken

@ProvideWithDagger("Parser")
class VanillaItemParser : Parser<ItemForm?> {

    override suspend fun parseElement(reader: JsonReader): ItemForm? {
        if (reader.peek() == JsonToken.NULL) {
            reader.skipValue()
            return null
        }

        var amount = 0
        var unlocalizedName = ""
        var localizedName = ""

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "a" -> amount = reader.nextInt()
                "uN" -> unlocalizedName = reader.nextString()
                "lN" -> localizedName = reader.nextString()
            }
        }
        reader.endObject()

        return ItemForm(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName
        )
    }
}
