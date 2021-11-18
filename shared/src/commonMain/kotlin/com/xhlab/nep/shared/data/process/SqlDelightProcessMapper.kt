package com.xhlab.nep.shared.data.process

import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.model.defaultJson
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.db.Process as ProcessEntity

class SqlDelightProcessMapper : Mapper<Process, ProcessEntity> {

    override fun map(element: Process): ProcessEntity {
        return ProcessEntity(
            element.id,
            element.name,
            element.targetOutput.unlocalizedName,
            element.targetOutput.localizedName,
            element.targetOutput.amount,
            element.getRecipeNodeCount(),
            defaultJson.encodeToString(ProcessSerializer, element)
        )
    }
}
