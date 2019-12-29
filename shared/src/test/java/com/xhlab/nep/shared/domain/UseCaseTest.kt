package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.xhlab.nep.shared.tests.util.LiveDataTestUtil
import com.xhlab.nep.shared.tests.util.MainCoroutineRule
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var useCase: UseCase<String, String>

    private val parameter = ""

    @Test
    fun invokeUseCase() {
        useCase = TestUseCase()

        val result = useCase.invoke(parameter)

        assertEquals(
            Resource.success(parameter),
            LiveDataTestUtil.getValue(result)
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveData() {
        useCase = TestUseCase()

        val mutableResult = MutableLiveData<Resource<String>>()
        useCase.invoke(parameter, mutableResult)

        assertEquals(
            Resource.success(parameter),
            LiveDataTestUtil.getValue(mutableResult)
        )
    }

    @Test
    fun invokeUseCaseFailed() {
        useCase = TestFailingUseCase()

        val result = useCase.invoke(parameter)

        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(result)?.status
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveDataFailed() {
        useCase = TestFailingUseCase()

        val mutableResult = MutableLiveData<Resource<String>>()
        useCase.invoke(parameter, mutableResult)

        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(mutableResult)?.status
        )
    }

    class TestUseCase : UseCase<String, String>() {
        override suspend fun execute(params: String): String {
            return params
        }
    }

    class TestFailingUseCase : UseCase<String, String>() {
        override suspend fun execute(params: String): String {
            throw RuntimeException()
        }
    }
}