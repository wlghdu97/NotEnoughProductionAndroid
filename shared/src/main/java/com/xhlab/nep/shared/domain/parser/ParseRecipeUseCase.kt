package com.xhlab.nep.shared.domain.parser

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import com.google.gson.stream.JsonReader
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.Cancelable
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.parser.*
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class ParseRecipeUseCase @Inject constructor(
    private val machineRecipeParser: MachineRecipeParser,
    private val shapedRecipeParser: ShapedRecipeParser,
    private val shapelessRecipeParser: ShapelessRecipeParser,
    private val shapedOreRecipeParser: ShapedOreRecipeParser,
    private val shapelessOreRecipeParser: ShapelessOreRecipeParser,
    private val replacementListParser: ReplacementListParser,
    private val elementRepo: ElementRepo,
    private val machineRepo: MachineRepo,
    private val generalPreference: GeneralPreference
) : MediatorUseCase<InputStream, String>(), Cancelable {

    private var job: Job? = null

    @ExperimentalCoroutinesApi
    override fun executeInternal(params: InputStream) = liveData<Resource<String>>(Dispatchers.IO) {
        // save job to support cancellation
        job = coroutineContext[Job]

        // mark db is dirty
        generalPreference.setDBLoaded(false)

        // delete all previous elements
        emitLog("deleting previous elements")
        machineRepo.deleteAll()
        elementRepo.deleteAll()

        val reader = JsonReader(params.bufferedReader())
        val startTime = System.currentTimeMillis()

        reader.beginObject()
        emitLog("source start. ${reader.nextName()}")

        while (reader.hasNext()) {
            reader.beginArray()
            var index = 0
            while (reader.hasNext()) {
                reader.beginObject()
                while (reader.hasNext()) {
                    reader.nextName()
                    val type = reader.nextString()
                    parse(type, reader)
                }
                reader.endObject()
                index += 1
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
    private suspend fun LiveDataScope<Resource<String>>.parse(type: String, reader: JsonReader) {
        when (type) {
            "shaped" -> shapedRecipeParser.parse(type, reader)
            "shapeless" -> shapelessRecipeParser.parse(type, reader)
            "shapedOre" -> shapedOreRecipeParser.parse(type, reader)
            "shapelessOre" -> shapelessOreRecipeParser.parse(type, reader)
            "replacements" -> replacementListParser.parse(reader)
            else -> machineRecipeParser.parse(type, reader)
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