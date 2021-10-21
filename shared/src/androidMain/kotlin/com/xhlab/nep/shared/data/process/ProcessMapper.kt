package com.xhlab.nep.shared.data.process

import com.google.gson.Gson
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.Mapper
import javax.inject.Inject
import com.xhlab.nep.shared.db.Process as ProcessEntity

class ProcessMapper @Inject constructor(
    private val gson: Gson
) : Mapper<ProcessEntity, Process> {

    override fun map(element: ProcessEntity): Process {
        return gson.fromJson(element.json, Process::class.java)
    }
}
