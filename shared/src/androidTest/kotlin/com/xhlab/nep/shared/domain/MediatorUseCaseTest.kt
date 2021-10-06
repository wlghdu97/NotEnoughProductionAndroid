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

    private val parameter = "param1"
    private val parameter2 = "param2"

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

    @Test
    fun executeMultiple() {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(parameter)
        useCase.execute(parameter2)

        assertEquals(
            Resource.success(parameter2),
            LiveDataTestUtil.getValue(result)
        )
    }

    class TestMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): LiveData<Resource<String>> {
            return MutableLiveData(Resource.success(params))
        }
    }

    class TestFailingMediatorUseCase : MediatorUseCase<String, String>() {
        override fun executeInternal(params: String): LiveData<Resource<String>> {
            throw RuntimeException()
        }
    }
}
