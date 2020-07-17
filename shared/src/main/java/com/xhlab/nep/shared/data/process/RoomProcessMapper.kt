package com.xhlab.nep.shared.data.process

import com.google.gson.Gson
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.db.entity.ProcessEntity
import javax.inject.Inject

class RoomProcessMapper @Inject constructor(
    private val gson: Gson
) : Mapper<Process, ProcessEntity> {

    override fun map(element: Process): ProcessEntity {
        return ProcessEntity(
            element.id,
            element.name,
            element.targetOutput.unlocalizedName,
            element.targetOutput.localizedName,
            element.targetOutput.amount,
            element.getRecipeNodeCount(),
            element.getSubProcessIds().size,
            gson.toJson(element)
        )
    }
}