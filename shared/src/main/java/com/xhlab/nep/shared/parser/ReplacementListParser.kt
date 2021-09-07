package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.parser.oredict.ReplacementParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.produce
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ReplacementListParser @Inject constructor(
    private val replacementParser: ReplacementParser,
    private val oreDictRepo: OreDictRepo
) {
    @ExperimentalCoroutinesApi
    suspend fun parse(reader: JsonReader) = CoroutineScope(coroutineContext).produce {
        send("parsing replacement list")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val replacementList = replacementParser.parseElements(reader)
                // insert replacement list into db
                send("inserting ${replacementList.size} replacements")
                oreDictRepo.insertReplacements(replacementList)
            } else {
                reader.skipValue()
            }
        }
    }
}
