package com.xhlab.nep.shared.parser.oredict

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class OreDictItemParser @Inject constructor() : Parser<Element> {

    override suspend fun parseElement(reader: JsonReader): Element {
        return if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            parseOreDictNameList(reader)
        } else {
            parseItemInternal(reader)
        }
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

    private fun parseOreDictNameList(reader: JsonReader): OreDictElement {
        val nameList = ArrayList<String>()
        reader.beginArray()
        while(reader.hasNext()) {
            nameList.add(reader.nextString())
        }
        reader.endArray()
        return OreDictElement(
            amount = 1,
            oreDictNameList = nameList
        )
    }
}