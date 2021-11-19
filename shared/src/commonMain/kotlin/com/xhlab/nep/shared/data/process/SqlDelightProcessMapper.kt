package com.xhlab.nep.shared.data.process

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import kotlinx.serialization.json.Json
import com.xhlab.nep.shared.db.Process as ProcessEntity

class SqlDelightProcessMapper(private val json: Json) : Mapper<Process, ProcessEntity> {

    override fun map(element: Process): ProcessEntity {
        return ProcessEntity(
            element.id,
            element.name,
            element.targetOutput.unlocalizedName,
            element.targetOutput.localizedName,
            element.targetOutput.amount,
            element.getRecipeNodeCount(),
            json.encodeToString(ProcessSerializer, element)
        )
    }
}
