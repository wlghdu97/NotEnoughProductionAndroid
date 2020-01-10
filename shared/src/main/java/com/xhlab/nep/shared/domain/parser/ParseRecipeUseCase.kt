package com.xhlab.nep.shared.domain.parser

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import com.google.gson.stream.JsonReader
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.domain.Cancelable
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ParseRecipeUseCase @Inject constructor(
    private val gregtechRecipeParser: GregtechRecipeParser,
    private val shapedRecipeParser: ShapedRecipeParser,
    private val shapelessRecipeParser: ShapelessRecipeParser,
    private val elementRepo: ElementRepo,
    private val gregtechRepo: GregtechRepo,
    private val generalPreference: GeneralPreference
) : MediatorUseCase<InputStream, String>(), Cancelable {

    private val io = Dispatchers.IO

    private var job: Job? = null

    @ExperimentalCoroutinesApi
    override fun executeInternal(params: InputStream) = liveData<Resource<String>>(io) {
        // save job to support cancellation
        job = coroutineContext[Job]

        // mark db is dirty
        generalPreference.setDBLoaded(false)

        // delete all previous elements
        emitLog("deleting previous elements")
        gregtechRepo.deleteGregtechMachines()
        elementRepo.deleteAll()

        val reader = JsonReader(params.bufferedReader())
        val startTime = System.currentTimeMillis()

        reader.beginObject()
        emitLog("source start. ${reader.nextName()}")

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

        // mark db is successfully loaded
        generalPreference.setDBLoaded(true)

        val elapsedTime = System.currentTimeMillis() - startTime
        emitLog("done! elapsed time : ${elapsedTime / 1000} sec", Resource.Status.SUCCESS)
    }

    override fun cancel() {
        job?.cancel()

        val message = "job canceled."
        Timber.i(message)
        result.value = Resource.success(message)
    }

    @ExperimentalCoroutinesApi
    private suspend fun LiveDataScope<Resource<String>>.parse(name: String, reader: JsonReader) {
        when (name) {
            "machines" -> gregtechRecipeParser.parse(reader)
            "shaped" -> shapedRecipeParser.parse(reader)
            "shapeless" -> shapelessRecipeParser.parse(reader)
            else -> return
        }.apply {
            consumeEach { emitLog(it) }
        }
    }

    private suspend fun LiveDataScope<Resource<String>>.emitLog(
        log: String,
        status: Resource.Status = Resource.Status.LOADING
    ) {
        Timber.i(log)
        emit(Resource(status, log, null))
    }
}