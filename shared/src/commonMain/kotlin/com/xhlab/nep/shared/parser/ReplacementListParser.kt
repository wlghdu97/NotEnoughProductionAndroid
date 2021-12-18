package com.xhlab.nep.shared.parser

import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.parser.oredict.ReplacementParser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.parser.stream.JsonToken
import kotlinx.coroutines.flow.flow
import kr.sparkweb.multiplatform.annotation.ProvideWithDagger

@ProvideWithDagger("Parser")
class ReplacementListParser constructor(
    private val replacementParser: ReplacementParser,
    private val oreDictRepo: OreDictRepo
) {
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
