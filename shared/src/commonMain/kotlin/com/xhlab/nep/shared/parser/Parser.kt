package com.xhlab.nep.shared.parser

import com.xhlab.nep.shared.parser.stream.JsonReader

interface Parser<T> {
    suspend fun parseElement(reader: JsonReader): T
    suspend fun parseElements(reader: JsonReader): List<T> {
        val itemList = ArrayList<T>()
        reader.beginArray()
        while (reader.hasNext()) {
            itemList.add(parseElement(reader))
        }
        reader.endArray()
        return itemList
    }
}
