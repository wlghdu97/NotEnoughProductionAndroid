package com.xhlab.nep.shared.domain.process

import android.util.Base64
import com.google.gson.Gson
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.BaseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.zip.Inflater
import javax.inject.Inject

class ImportProcessStringUseCase @Inject constructor(
    private val gson: Gson,
    private val processRepo: ProcessRepo
) : BaseUseCase<ImportProcessStringUseCase.Parameter, Unit>() {

    override suspend fun execute(params: Parameter) = withContext(Dispatchers.IO) {
        if (params.string.isEmpty()) {
            throw IllegalArgumentException()
        }
        val decodedInput = Base64.decode(params.string, Base64.NO_WRAP)
        if (decodedInput.isEmpty()) {
            throw IllegalArgumentException()
        }
        val inflater = Inflater()
        inflater.setInput(decodedInput)
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val baos = ByteArrayOutputStream(decodedInput.size)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            baos.write(buffer, 0, count)
        }
        inflater.end()

        val returnValues = baos.toByteArray()
        baos.close()
        val string = returnValues.toString(Charsets.UTF_8)
        val process = gson.fromJson(string, Process::class.java)
        if (process != null) {
            processRepo.insertProcess(process)
        } else {
            throw NullPointerException()
        }
    }

    data class Parameter(val string: String)
}
