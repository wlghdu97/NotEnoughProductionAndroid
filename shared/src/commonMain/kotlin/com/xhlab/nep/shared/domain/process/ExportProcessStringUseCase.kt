package com.xhlab.nep.shared.domain.process

import com.xhlab.multiplatform.annotation.ProvideWithDagger
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseUseCase
import com.xhlab.nep.shared.util.Base64Factory
import com.xhlab.nep.shared.util.Deflater

@ProvideWithDagger("ProcessDomain")
class ExportProcessStringUseCase constructor(
    private val processRepo: ProcessRepo
) : BaseUseCase<ExportProcessStringUseCase.Parameter, String>() {

    override suspend fun execute(params: Parameter): String {
        val json = processRepo.exportProcessString(params.processId)
        return if (json != null) {
            val input = json.encodeToByteArray()
            val compressed = Deflater(input).deflate()
            val encoder = Base64Factory.noWrapEncoder
            encoder.encode(compressed).decodeToString()
        } else {
            "Invalid string"
        }
    }

    data class Parameter(val processId: String)
}
