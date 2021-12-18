package com.xhlab.nep.shared.parser.element

import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.shared.parser.Parser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.parser.stream.JsonToken
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger

@ProvideWithDagger("Parser")
class FluidParser : Parser<FluidForm> {

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
