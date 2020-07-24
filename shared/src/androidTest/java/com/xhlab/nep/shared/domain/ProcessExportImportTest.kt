package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.ExportProcessStringUseCase
import com.xhlab.nep.shared.domain.process.ImportProcessStringUseCase
import com.xhlab.nep.shared.parser.process.ProcessDeserializer
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.util.LiveDataTestUtil
import com.xhlab.nep.shared.util.MainCoroutineRule
import com.xhlab.nep.shared.util.Resource
import com.xhlab.test_shared.ProcessData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor

@RunWith(AndroidJUnit4::class)
class ProcessExportImportTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var gson: Gson

    private lateinit var processCaptor: ArgumentCaptor<Process>
    private lateinit var processRepo: ProcessRepo

    private lateinit var exportUseCase: ExportProcessStringUseCase
    private lateinit var importUseCase: ImportProcessStringUseCase

    private val processId = ProcessData.processPE.id

    @Before
    fun prepare() {
        gson = GsonBuilder()
            .registerTypeAdapter(Process::class.java, ProcessSerializer())
            .registerTypeAdapter(Process::class.java, ProcessDeserializer())
            .create()

        processCaptor = ArgumentCaptor.forClass(Process::class.java)

        processRepo = mock {
            onBlocking { exportProcessString(ProcessData.processPE.id) }
                .doReturn(gson.toJson(ProcessData.processPE))
            onBlocking { insertProcess(any()) }
                .doReturn(Unit)
        }

        exportUseCase = ExportProcessStringUseCase(processRepo)
        importUseCase = ImportProcessStringUseCase(gson, processRepo)
    }

    @Test
    fun exportThenImport() {
        val exportResult = exportUseCase.invoke(ExportProcessStringUseCase.Parameter(processId))
        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(exportResult)?.status
        )

        val json = exportResult.value?.data
        assertNotNull(json)
        val importResult = importUseCase.invoke(ImportProcessStringUseCase.Parameter(json!!))
        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(importResult)?.status
        )

        runBlocking {
            verify(processRepo, times(1)).exportProcessString(processId)
            verify(processRepo, times(1)).insertProcess(capture(processCaptor))
        }

        val processes = processCaptor.allValues
        assert(checkProcessSame(processes[0]))
    }

    @Test
    fun importWithEmptyText() {
        val result = importUseCase.invoke(ImportProcessStringUseCase.Parameter(""))
        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(result)?.status
        )
    }

    @Test
    fun importWithShortText() {
        val result = importUseCase.invoke(ImportProcessStringUseCase.Parameter("aaaa"))
        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(result)?.status
        )
    }

    private fun checkProcessSame(newProcess: Process): Boolean{
        val process = ProcessData.processPE
        return (process.id == newProcess.id &&
                process.name == newProcess.name &&
                process.getRecipeNodeCount() == newProcess.getRecipeNodeCount() &&
                process.getEdgesCount() == newProcess.getEdgesCount())
    }
}