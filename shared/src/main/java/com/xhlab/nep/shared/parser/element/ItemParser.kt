package com.xhlab.nep.shared.parser.element

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Item
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class ItemParser @Inject constructor() : Parser<Item> {

    override suspend fun parseElement(reader: JsonReader): Item {
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
                    metaData = String.format("%.2f%%", reader.nextDouble() * 100) // drop chance
            }
        }
        reader.endObject()

        return Item(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName,
            metaData = metaData
        )
    }
}