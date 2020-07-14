package com.xhlab.nep.model.process.recipes

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process

class ProcessRecipe(element: ElementView, process: Process) : Recipe {
    private val innerElement: Element =
        ElementImpl(
            id = element.id,
            localizedName = element.localizedName,
            unlocalizedName = element.unlocalizedName,
            type = element.type,
            metaData = element.metaData
        )

    private val processElement: Element =
        ElementImpl(
            id = 0,
            localizedName = process.name,
            unlocalizedName = process.id,
            type = Process.PROCESS_REFERENCE,
            metaData = null
        )

    fun getProcessId() = processElement.unlocalizedName

    override fun getInputs(): List<Element> {
        return listOf(processElement)
    }

    override fun getOutput(): List<Element> {
        return listOf(innerElement)
    }
}