package com.xhlab.nep.shared.domain.parser

import co.touchlab.kermit.Logger
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.domain.BaseMediatorUseCase
import com.xhlab.nep.shared.parser.*
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.epochMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kr.sparkweb.multiplatform.domain.Cancellable
import kr.sparkweb.multiplatform.util.Resource

class ParseRecipeUseCase constructor(
    private val machineRecipeParser: MachineRecipeParser,
    private val shapedRecipeParser: ShapedRecipeParser,
    private val shapelessRecipeParser: ShapelessRecipeParser,
    private val shapedOreRecipeParser: ShapedOreRecipeParser,
    private val shapelessOreRecipeParser: ShapelessOreRecipeParser,
    private val replacementListParser: ReplacementListParser,
    private val furnaceRecipeParser: FurnaceRecipeParser,
    private val elementRepo: ElementRepo,
    private val machineRepo: MachineRepo,
    private val generalPreference: GeneralPreference,
    private val io: CoroutineDispatcher
) : BaseMediatorUseCase<() -> JsonReader, String>(), Cancellable {

    override suspend fun executeInternal(params: () -> JsonReader) = channelFlow {
        // mark db is dirty
        generalPreference.setDBLoaded(false)

        // delete all previous elements
        emitLog("deleting previous elements")
        machineRepo.deleteAll()
        elementRepo.deleteAll()

        val startTime = epochMillis
        val reader = params()

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
        reader.close()

        // mark db is successfully loaded
        generalPreference.setDBLoaded(true)

        val elapsedTime = epochMillis - startTime
        emitLog("done! elapsed time : ${elapsedTime / 1000} sec", Resource.Status.SUCCESS)
    }.flowOn(io)

    override fun onCancellation() {
        val message = "job canceled."
        Logger.i(message)
        result.value = Resource.success(message)
    }

    private suspend fun ProducerScope<Resource<String>>.parse(type: String, reader: JsonReader) {
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

    private suspend fun ProducerScope<Resource<String>>.emitLog(
        log: String,
        status: Resource.Status = Resource.Status.LOADING
    ) {
        Logger.i(log)
        send(Resource(status, log, null))
    }
}
