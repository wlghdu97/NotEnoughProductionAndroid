package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.xhlab.nep.model.Recipe
import kotlinx.coroutines.flow.Flow

abstract class RecipeParser<T : Recipe> : Parser<T> {
    abstract suspend fun parse(type: String, reader: JsonReader): Flow<String>
}
