package com.xhlab.nep.shared.parser.oredict

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.model.form.ReplacementForm
import com.xhlab.nep.shared.parser.Parser
import javax.inject.Inject

class ReplacementParser @Inject constructor() : Parser<ReplacementForm> {

    override suspend fun parseElement(reader: JsonReader): ReplacementForm {
        var oreDictName = ""
        var elementList: List<ElementForm> = emptyList()

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "name" -> oreDictName = reader.nextString()
                "reps" -> elementList = parseElementList(reader)
            }
        }
        reader.endObject()

        return ReplacementForm(
            oreDictName = oreDictName,
            elementList = elementList
        )
    }

    private fun parseElementList(reader: JsonReader): List<ElementForm> {
        val list = ArrayList<ElementForm>()
        reader.beginArray()
        while (reader.hasNext()) {
            list.add(parseItemInternal(reader))
        }
        reader.endArray()
        return list
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
}
