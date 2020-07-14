package com.xhlab.nep.model.process.recipes

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process

class ProcessRecipe : Recipe {
    private val innerElement: Element
    private val processElement: Element

    constructor(element: ElementView, processId: String, processName: String) {
        innerElement = ElementImpl(
            id = element.id,
            localizedName = element.localizedName,
            unlocalizedName = element.unlocalizedName,
            type = element.type,
            metaData = element.metaData
        )
        processElement = ElementImpl(
            id = 0,
            localizedName = processName,
            unlocalizedName = processId,
            type = Process.PROCESS_REFERENCE,
            metaData = null,
            amount = 0
        )
    }

    constructor(element: ElementView, process: Process) : this(element, process.id, process.name)

    fun getProcessId() = processElement.unlocalizedName

    override fun getInputs(): List<Element> {
        return listOf(processElement)
    }

    override fun getOutput(): List<Element> {
        return listOf(innerElement)
    }
}