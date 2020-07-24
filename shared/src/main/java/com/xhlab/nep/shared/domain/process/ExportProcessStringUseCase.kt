package com.xhlab.nep.shared.domain.process

import android.util.Base64
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.UseCase
import java.util.zip.Deflater
import javax.inject.Inject

class ExportProcessStringUseCase @Inject constructor(
    private val processRepo: ProcessRepo
) : UseCase<ExportProcessStringUseCase.Parameter, String>() {

    override suspend fun execute(params: Parameter): String {
        val json = processRepo.exportProcessString(params.processId)
        return if (json != null) {
            val input = json.toByteArray()
            val deflater = Deflater().apply {
                setInput(input)
                finish()
            }
            val bytesCompressed = ByteArray(Short.MAX_VALUE.toInt())
            val compressedByteLength = deflater.deflate(bytesCompressed)
            val returnValues = ByteArray(compressedByteLength)
            System.arraycopy(bytesCompressed, 0, returnValues, 0, compressedByteLength)
            Base64.encodeToString(returnValues, Base64.NO_WRAP)
        } else {
            "Invalid string"
        }
    }

    data class Parameter(val processId: String)
}