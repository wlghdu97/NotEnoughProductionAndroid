package com.xhlab.nep.shared.domain.process

import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.util.Base64Factory
import com.xhlab.nep.shared.util.Inflater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ImportProcessStringUseCase @Inject constructor(
    private val processRepo: ProcessRepo,
    private val json: Json
) : BaseUseCase<ImportProcessStringUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) = withContext(Dispatchers.IO) {
        if (params.string.isEmpty()) {
            throw IllegalArgumentException()
        }
        val decoder = Base64Factory.noWrapDecoder
        val decodedInput = decoder.decode(params.string.toByteArray())
        if (decodedInput.isEmpty()) {
            throw IllegalArgumentException()
        }

        val inflated = Inflater(decodedInput).inflate()
        val string = inflated.toString(Charsets.UTF_8)
        val process = json.decodeFromString(ProcessSerializer, string)
        processRepo.insertProcess(process)
    }

    data class Parameter(val string: String)
}
