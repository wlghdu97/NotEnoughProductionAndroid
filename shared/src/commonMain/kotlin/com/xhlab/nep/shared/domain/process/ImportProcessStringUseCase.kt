package com.xhlab.nep.shared.domain.process

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.util.Base64Factory
import com.xhlab.nep.shared.util.Inflater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@ProvideWithDagger("ProcessDomain")
class ImportProcessStringUseCase constructor(
    private val processRepo: ProcessRepo,
    private val json: Json
) : BaseUseCase<ImportProcessStringUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) = withContext(Dispatchers.Default) {
        if (params.string.isEmpty()) {
            throw IllegalArgumentException()
        }
        val decoder = Base64Factory.noWrapDecoder
        val decodedInput = decoder.decode(params.string.encodeToByteArray())
        if (decodedInput.isEmpty()) {
            throw IllegalArgumentException()
        }

        val inflated = Inflater(decodedInput).inflate()
        val string = inflated.decodeToString()
        val process = json.decodeFromString(ProcessSerializer, string)
        processRepo.insertProcess(process)
    }

    data class Parameter(val string: String)
}
