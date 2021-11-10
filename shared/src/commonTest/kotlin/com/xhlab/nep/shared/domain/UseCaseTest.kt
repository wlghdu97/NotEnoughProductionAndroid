package com.xhlab.nep.shared.domain

import co.touchlab.kermit.Logger
import com.xhlab.multiplatform.domain.UseCase
import com.xhlab.multiplatform.util.Resource
import com.xhlab.nep.shared.util.runBlockingTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.take
import kotlin.test.Test
import kotlin.test.assertEquals

class UseCaseTest {

    private lateinit var useCase: UseCase<String, String>

    private val parameter = ""

    @Test
    fun invokeUseCase() = runBlockingTest {
        useCase = TestUseCase()

        val result = useCase.invoke(Dispatchers.Main, parameter)

        assertEquals(
            Resource.success(parameter),
            result.take(2).lastOrNull()
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveData() = runBlockingTest {
        useCase = TestUseCase()

        val mutableResult = MutableStateFlow<Resource<String>>(Resource.loading(null))
        useCase.invoke(coroutineContext, parameter, mutableResult)

        assertEquals(
            Resource.success(parameter),
            mutableResult.take(2).lastOrNull()
        )
    }

    @Test
    fun invokeUseCaseFailed() = runBlockingTest {
        useCase = TestFailingUseCase()

        val result = useCase.invoke(coroutineContext, parameter)

        assertEquals(
            Resource.Status.ERROR,
            result.take(2).lastOrNull()?.status
        )
    }

    @Test
    fun invokeUseCaseWithMutableLiveDataFailed() = runBlockingTest {
        useCase = TestFailingUseCase()

        val mutableResult = MutableStateFlow<Resource<String>>(Resource.loading(null))
        useCase.invoke(coroutineContext, parameter, mutableResult)

        assertEquals(
            Resource.Status.ERROR,
            mutableResult.take(2).lastOrNull()?.status
        )
    }

    class TestUseCase : UseCase<String, String>() {

        override suspend fun execute(params: String): String {
            return params
        }

        override fun onException(exception: Throwable) {
            Logger.e(exception) {
                "Use case crashed."
            }
        }
    }

    class TestFailingUseCase : UseCase<String, String>() {

        override suspend fun execute(params: String): String {
            throw RuntimeException()
        }

        override fun onException(exception: Throwable) = Unit
    }
}
