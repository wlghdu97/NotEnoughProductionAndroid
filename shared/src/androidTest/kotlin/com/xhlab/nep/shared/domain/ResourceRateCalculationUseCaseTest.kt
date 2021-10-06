package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.xhlab.nep.shared.domain.process.ResourceRateCalculationUseCase
import com.xhlab.nep.shared.tests.util.MainCoroutineRule
import com.xhlab.nep.shared.util.Resource
import com.xhlab.test.shared.ProcessData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ResourceRateCalculationUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: ResourceRateCalculationUseCase

    @Before
    fun prepare() {
        useCase = ResourceRateCalculationUseCase()
    }

    @Test
    fun executeSuccessfully() = runBlocking {
        val parameters = ResourceRateCalculationUseCase.Parameter(
            process = ProcessData.processPE
        )
        val result = useCase.invokeInstant(parameters)

        assertEquals(
            Resource.Status.SUCCESS,
            result.status
        )
    }
}
