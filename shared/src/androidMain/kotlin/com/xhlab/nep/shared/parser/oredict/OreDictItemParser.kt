package com.xhlab.nep.shared.parser.oredict

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.model.form.OreDictForm
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class OreDictItemParser @Inject constructor() : Parser<ElementForm> {

    override suspend fun parseElement(reader: JsonReader): ElementForm {
        return if (reader.peek() == JsonToken.BEGIN_ARRAY) {
            parseOreDictNameList(reader)
        } else {
            parseItemInternal(reader)
        }
    }

    private fun parseItemInternal(reader: JsonReader): ItemForm {
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

        return ItemForm(
            amount = amount,
            unlocalizedName = unlocalizedName,
            localizedName = localizedName
        )
    }

    private fun parseOreDictNameList(reader: JsonReader): OreDictForm {
        val nameList = ArrayList<String>()
        reader.beginArray()
        while (reader.hasNext()) {
            nameList.add(reader.nextString())
        }
        reader.endArray()
        return OreDictForm(
            amount = 1,
            oreDictNameList = nameList
        )
    }
}
