package com.xhlab.nep.shared.data.process

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import kotlinx.serialization.json.Json
import com.xhlab.nep.shared.db.Process as ProcessEntity

class ProcessMapper(private val json: Json) : Mapper<ProcessEntity, Process> {

    override fun map(element: ProcessEntity): Process {
        return json.decodeFromString(ProcessSerializer, element.json)
    }
}
