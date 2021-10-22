package com.xhlab.nep.shared.parser

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.parser.oredict.ReplacementParser
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReplacementListParser @Inject constructor(
    private val replacementParser: ReplacementParser,
    private val oreDictRepo: OreDictRepo
) {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun parse(reader: JsonReader) = flow {
        emit("parsing replacement list")
        while (reader.hasNext()) {
            if (reader.peek() == JsonToken.BEGIN_ARRAY) {
                val replacementList = replacementParser.parseElements(reader)
                // insert replacement list into db
                emit("inserting ${replacementList.size} replacements")
                oreDictRepo.insertReplacements(replacementList)
            } else {
                reader.skipValue()
            }
        }
    }
}
