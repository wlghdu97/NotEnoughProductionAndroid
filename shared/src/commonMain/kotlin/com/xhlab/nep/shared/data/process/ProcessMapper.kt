package com.xhlab.nep.shared.data.process

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import kotlinx.serialization.json.Json
import com.xhlab.nep.shared.db.Process as ProcessEntity

class ProcessMapper : Mapper<ProcessEntity, Process> {

    override fun map(element: ProcessEntity): Process {
        return Json.decodeFromString(ProcessSerializer, element.json)
    }
}
