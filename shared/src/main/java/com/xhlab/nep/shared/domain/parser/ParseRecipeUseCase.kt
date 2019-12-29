package com.xhlab.nep.shared.domain.parser

import android.util.Log
import com.google.gson.stream.JsonReader
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import com.xhlab.nep.shared.domain.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class ParseRecipeUseCase @Inject internal constructor(
    private val gregtechRecipeParser: GregtechRecipeParser,
    private val shapedRecipeParser: ShapedRecipeParser,
    private val shapelessRecipeParser: ShapelessRecipeParser
) : UseCase<InputStream, Unit>() {

    private val io = Dispatchers.IO

    override suspend fun execute(params: InputStream) = withContext(io) {
        val reader = JsonReader(params.bufferedReader())
        val startTime = System.currentTimeMillis()

        reader.beginObject()
        val sourcesName = reader.nextName()
        Timber.i("source start. $sourcesName")
        while (reader.hasNext()) {
            reader.beginArray()
            while (reader.hasNext()) {
                reader.beginObject()
                while (reader.hasNext()) {
                    when (val name = reader.nextName()) {
                        "type" -> parse(reader.nextString(), reader)
                        else -> parse(name, reader)
                    }
                }
                reader.endObject()
            }
            reader.endArray()
        }
        reader.endObject()
        val elapsedTime = System.currentTimeMillis() - startTime
        Timber.i("done! elapsed time : ${elapsedTime / 1000} sec")
        return@withContext
    }

    private suspend fun parse(name: String, reader: JsonReader) {
        when (name) {
            "machines" -> gregtechRecipeParser.parse(reader)
            "shaped" -> shapedRecipeParser.parse(reader)
            "shapeless" -> shapelessRecipeParser.parse(reader)
        }
    }
}