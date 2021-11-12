package com.xhlab.nep.shared.parser.element

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class FluidParser @Inject constructor() : Parser<FluidForm> {

    override suspend fun parseElement(reader: JsonReader): FluidForm {
        var amount = 0
        var unlocalizedName = ""
        var localizedName = ""

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
            }
        }
        reader.endObject()

        return FluidForm(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName
        )
    }
}
