package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xhlab.nep.shared.tests.util.LiveDataTestUtil
import com.xhlab.nep.shared.tests.util.MainCoroutineRule
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MediatorUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: MediatorUseCase<String, String>

    private val parameter = ""

    @Test
    fun executeSuccessfully() {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(parameter)

        assertEquals(
            Resource.success(parameter),
            LiveDataTestUtil.getValue(result)
        )
    }

    @Test
    fun executeWithObserveOnly() {
        useCase = TestMediatorUseCase()

        val result = useCase.observeOnly(Resource.Status.SUCCESS)
        useCase.execute(parameter)

        assertEquals(
            parameter,
            LiveDataTestUtil.getValue(result)
        )
    }

    @Test
    fun executeFailed() {
        useCase = TestFailingMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(parameter)

        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(result)?.status
        )
    }

    class TestMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): LiveData<String> {
            return MutableLiveData(params)
        }
    }

    class TestFailingMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): LiveData<String> {
            throw RuntimeException()
        }
    }
}