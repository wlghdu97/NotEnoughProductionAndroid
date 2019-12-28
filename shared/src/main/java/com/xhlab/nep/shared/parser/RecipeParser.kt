package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Recipe

abstract class RecipeParser<T : Recipe> : Parser<T> {
    abstract suspend fun parse(reader: JsonReader)
}