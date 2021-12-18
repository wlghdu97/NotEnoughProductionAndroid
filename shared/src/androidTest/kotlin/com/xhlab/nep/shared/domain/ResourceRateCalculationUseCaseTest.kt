package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.xhlab.nep.shared.domain.process.ResourceRateCalculationUseCase
import com.xhlab.nep.shared.util.runBlockingTest
import com.xhlab.test.shared.ProcessData
import kr.sparkweb.multiplatform.util.Resource
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class ResourceRateCalculationUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var useCase: ResourceRateCalculationUseCase

    @Before
    fun prepare() {
        useCase = ResourceRateCalculationUseCase()
    }

    @Test
    fun executeSuccessfully() = runBlockingTest {
        val parameters = ResourceRateCalculationUseCase.Parameter(
            process = ProcessData.processPE
        )
        val result = useCase.invokeInstant(parameters)
        assertEquals(Resource.Status.SUCCESS, result.status)
    }
}
