package com.xhlab.nep.shared.parser.element

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class VanillaItemParser @Inject constructor() : Parser<ItemForm?> {

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
