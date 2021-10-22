package com.xhlab.nep.shared.domain.parser

import com.google.gson.stream.JsonReader
import com.xhlab.multiplatform.domain.Cancellable
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import com.xhlab.nep.shared.parser.*
import com.xhlab.nep.shared.preference.GeneralPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class ParseRecipeUseCase @Inject constructor(
    private val machineRecipeParser: MachineRecipeParser,
    private val shapedRecipeParser: ShapedRecipeParser,
    private val shapelessRecipeParser: ShapelessRecipeParser,
    private val shapedOreRecipeParser: ShapedOreRecipeParser,
    private val shapelessOreRecipeParser: ShapelessOreRecipeParser,
    private val replacementListParser: ReplacementListParser,
    private val furnaceRecipeParser: FurnaceRecipeParser,
    private val elementRepo: ElementRepo,
    private val machineRepo: MachineRepo,
    private val generalPreference: GeneralPreference
) : BaseMediatorUseCase<InputStream, String>(), Cancellable {

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun executeInternal(params: InputStream) = flow {
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

        // close stream
        params.close()

        // mark db is successfully loaded
        generalPreference.setDBLoaded(true)

        val elapsedTime = System.currentTimeMillis() - startTime
        emitLog("done! elapsed time : ${elapsedTime / 1000} sec", Resource.Status.SUCCESS)
    }.flowOn(Dispatchers.IO)

    override fun onCancellation() {
        val message = "job canceled."
        Timber.i(message)
        result.value = Resource.success(message)
    }

    private suspend fun FlowCollector<Resource<String>>.parse(type: String, reader: JsonReader) {
        when (type) {
            "shaped" -> shapedRecipeParser.parse(type, reader)
            "shapeless" -> shapelessRecipeParser.parse(type, reader)
            "shapedOre" -> shapedOreRecipeParser.parse(type, reader)
            "shapelessOre" -> shapelessOreRecipeParser.parse(type, reader)
            "replacements" -> replacementListParser.parse(reader)
            "furnace" -> furnaceRecipeParser.parse(type, reader)
            else -> machineRecipeParser.parse(type, reader)
        }.apply {
            collect { emitLog(it) }
        }
    }

    private suspend fun FlowCollector<Resource<String>>.emitLog(
        log: String,
        status: Resource.Status = Resource.Status.LOADING
    ) {
        Timber.i(log)
        emit(Resource(status, log, null))
    }
}
