package com.xhlab.nep.shared.parser

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.parser.process.ProcessDeserializer
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.test_shared.ProcessData
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class ProcessSerializationTest {

    private lateinit var gson: Gson

    @Before
    fun prepare() {
        gson = GsonBuilder()
            .registerTypeAdapter(Process::class.java, ProcessSerializer())
            .registerTypeAdapter(Process::class.java, ProcessDeserializer())
            .create()
    }

    @Test
    fun deserializeGlass() {
        val json = gson.toJson(ProcessData.processGlass)
        val original = ProcessData.processGlass
        val new = gson.fromJson(json, Process::class.java)
        assertEquals(original.getRecipeNodeCount(), new.getRecipeNodeCount())
        assertEquals(original.getEdgesCount(), new.getEdgesCount())
    }

    @Test
    fun deserializePE() {
        val json = gson.toJson(ProcessData.processPE)
        val original = ProcessData.processPE
        val new = gson.fromJson(json, Process::class.java)
        assertEquals(original.getRecipeNodeCount(), new.getRecipeNodeCount())
        assertEquals(original.getEdgesCount(), new.getEdgesCount())
    }
}
