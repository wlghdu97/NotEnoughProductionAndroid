package com.xhlab.nep.shared.parser

import com.xhlab.nep.model.form.recipes.RecipeForm
import com.xhlab.nep.shared.parser.stream.JsonReader
import kotlinx.coroutines.flow.Flow

abstract class RecipeParser<T : RecipeForm> : Parser<T> {
    abstract suspend fun parse(type: String, reader: JsonReader): Flow<String>
}
