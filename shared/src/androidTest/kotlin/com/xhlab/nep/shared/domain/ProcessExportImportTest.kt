package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.shared.data.process.ProcessRepo
import com.xhlab.nep.shared.domain.process.ExportProcessStringUseCase
import com.xhlab.nep.shared.domain.process.ImportProcessStringUseCase
import com.xhlab.nep.shared.parser.process.ProcessSerializer
import com.xhlab.nep.shared.util.runBlockingTest
import com.xhlab.nep.shared.util.testJson
import com.xhlab.test.shared.ProcessData
import kr.sparkweb.multiplatform.util.Resource
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

    private lateinit var processCaptor: ArgumentCaptor<Process>
    private lateinit var processRepo: ProcessRepo

    private lateinit var exportUseCase: ExportProcessStringUseCase
    private lateinit var importUseCase: ImportProcessStringUseCase

    private val processId = ProcessData.processPE.id

    @Before
    fun prepare() {
        processCaptor = ArgumentCaptor.forClass(Process::class.java)

        processRepo = mock {
            onBlocking { exportProcessString(ProcessData.processPE.id) }
                .doReturn(testJson.encodeToString(ProcessSerializer, ProcessData.processPE))
            onBlocking { insertProcess(any()) }
                .doReturn(Unit)
        }

        exportUseCase = ExportProcessStringUseCase(processRepo)
        importUseCase = ImportProcessStringUseCase(processRepo, testJson)
    }

    @Test
    fun exportThenImport() = runBlockingTest {
        val exportResult =
            exportUseCase.invokeInstant(ExportProcessStringUseCase.Parameter(processId))
        assertEquals(Resource.Status.SUCCESS, exportResult.status)

        val json = exportResult.data
        assertNotNull(json)
        val importResult =
            importUseCase.invokeInstant(ImportProcessStringUseCase.Parameter(json!!))
        assertEquals(Resource.Status.SUCCESS, importResult.status)

        verify(processRepo, times(1)).exportProcessString(processId)
        verify(processRepo, times(1)).insertProcess(capture(processCaptor))

        val processes = processCaptor.allValues
        assert(checkProcessSame(processes[0]))
    }

    @Test
    fun importWithEmptyText() = runBlockingTest {
        val result = importUseCase.invokeInstant(ImportProcessStringUseCase.Parameter(""))
        assertEquals(Resource.Status.ERROR, result.status)
    }

    @Test
    fun importWithShortText() = runBlockingTest {
        val result = importUseCase.invokeInstant(ImportProcessStringUseCase.Parameter("aaaa"))
        assertEquals(Resource.Status.ERROR, result.status)
    }

    private fun checkProcessSame(newProcess: Process): Boolean {
        val process = ProcessData.processPE
        return (process.id == newProcess.id &&
                process.name == newProcess.name &&
                process.getRecipeNodeCount() == newProcess.getRecipeNodeCount() &&
                process.getEdgesCount() == newProcess.getEdgesCount())
    }
}
