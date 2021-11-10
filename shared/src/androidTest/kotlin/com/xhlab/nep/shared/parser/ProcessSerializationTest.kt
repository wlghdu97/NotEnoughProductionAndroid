package com.xhlab.nep.shared.parser

import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.parser.process.processJson
import com.xhlab.test.shared.ProcessData
import org.junit.Test
import kotlin.test.assertEquals

class ProcessSerializationTest {

    @Test
    fun deserializeGlass() {
        val json = processJson.encodeToString(ProcessSerializer, ProcessData.processGlass)
        val original = ProcessData.processGlass
        val new = processJson.decodeFromString(ProcessSerializer, json)
        assertEquals(original.getRecipeNodeCount(), new.getRecipeNodeCount())
        assertEquals(original.getEdgesCount(), new.getEdgesCount())
    }

    @Test
    fun deserializePE() {
        val json = processJson.encodeToString(ProcessSerializer, ProcessData.processPE)
        val original = ProcessData.processPE
        val new = processJson.decodeFromString(ProcessSerializer, json)
        assertEquals(original.getRecipeNodeCount(), new.getRecipeNodeCount())
        assertEquals(original.getEdgesCount(), new.getEdgesCount())
    }
}
