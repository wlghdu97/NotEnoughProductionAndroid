package com.xhlab.nep.shared.parser.oredict

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.oredict.Replacement
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class ReplacementParser @Inject constructor() : Parser<Replacement> {

    override suspend fun parseElement(reader: JsonReader): Replacement {
        var oreDictName = ""
        var elementList: List<Element> = emptyList()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> oreDictName = reader.nextString()
                "reps" -> elementList = parseElementList(reader)
            }
        }
        reader.endObject()

        return Replacement(
            oreDictName = oreDictName,
            elementList = elementList
        )
    }

    private fun parseElementList(reader: JsonReader): List<Element> {
        val list = ArrayList<Element>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(parseItemInternal(reader))
        }
        reader.endArray()
        return list
    }

    private fun parseItemInternal(reader: JsonReader): Item {
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

        return Item(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName
        )
    }
}
