package com.xhlab.nep.shared.domain

import co.touchlab.kermit.Logger
import com.xhlab.nep.shared.util.runBlockingTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kr.sparkweb.multiplatform.domain.MediatorUseCase
import kr.sparkweb.multiplatform.util.Resource
import kotlin.test.Test
import kotlin.test.assertEquals

class MediatorUseCaseTest {

    private lateinit var useCase: MediatorUseCase<String, String>

    private val parameter = "param1"
    private val parameter2 = "param2"

    @Test
    fun executeSuccessfully() = runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(Dispatchers.Main, parameter)

        assertEquals(
            Resource.success(parameter),
            result.take(2).lastOrNull()
        )
    }

    @Test
    fun executeWithObserveOnly() = runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observeOnly(Resource.Status.SUCCESS)
        useCase.execute(Dispatchers.Main, parameter)

        assertEquals(
            parameter,
            result.firstOrNull()
        )
    }

    @Test
    fun executeFailed() = runBlockingTest {
        useCase = TestFailingMediatorUseCase()

        val result = useCase.observe()
        useCase.execute(Dispatchers.Main, parameter)

        assertEquals(
            Resource.Status.ERROR,
            result.take(2).lastOrNull()?.status
        )
    }

    @Test
    fun executeMultipleWithCancelling() = runBlockingTest {
        useCase = TestMediatorUseCase()

        val result = useCase.observe()
        val job = useCase.execute(Dispatchers.Main, parameter)
        job.cancel()
        useCase.execute(Dispatchers.Main, parameter2)

        assertEquals(
            Resource.success(parameter2),
            result.take(2).lastOrNull()
        )
    }

    class TestMediatorUseCase : MediatorUseCase<String, String>() {

        override suspend fun executeInternal(params: String): Flow<Resource<String>> {
            return flowOf(Resource.success(params))
        }

        override fun onException(exception: Throwable) {
            Logger.e(exception) {
                "Mediator use case crashed."
            }
        }
    }

    class TestFailingMediatorUseCase : MediatorUseCase<String, String>() {

        override suspend fun executeInternal(params: String): Flow<Resource<String>> {
            throw RuntimeException()
        }

        override fun onException(exception: Throwable) = Unit
    }
}
