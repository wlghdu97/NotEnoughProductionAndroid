package com.xhlab.nep.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.xhlab.nep.shared.domain.MediatorUseCase
import com.xhlab.nep.shared.domain.UseCase
import com.xhlab.nep.shared.util.Resource
import com.xhlab.nep.tests.util.LiveDataTestUtil
import com.xhlab.nep.tests.util.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException

class BaseViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: FakeViewModel

    @Before
    fun prepare() {
        viewModel = FakeViewModel(
            TestUseCase(),
            TestFailingUseCase(),
            TestMediatorUseCase()
        )
    }

    @Test
    fun executeUseCase() {
        viewModel.executeUseCase()

        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(viewModel.actionResults)?.status
        )
    }

    @Test
    fun executeUseCaseFailed() {
        viewModel.executeFailingUseCase()

        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(viewModel.actionResults)?.status
        )
    }

    @Test
    fun executeUseCaseWithResult() {
        viewModel.executeUseCaseWithResult()

        assertEquals(
            Resource.success(RESULT),
            LiveDataTestUtil.getValue(viewModel.useCaseResult)
        )
    }

    @Test
    fun executeMediatorUseCase() {
        val result = viewModel.mediatorResult

        viewModel.executeMediatorUseCase()

        assertEquals(
            Resource.success(RESULT),
            LiveDataTestUtil.getValue(result)
        )
    }

    @Test
    fun executeMediatorUseCaseWithResult() {
        val result = viewModel.mediatorUseCaseResult

        viewModel.executeMediatorUseCaseWithResult()

        assertEquals(
            Resource.success(RESULT),
            LiveDataTestUtil.getValue(result)
        )
    }

    private class FakeViewModel(
        private val useCase: TestUseCase,
        private val failingUseCase: TestFailingUseCase,
        private val mediatorUseCase: TestMediatorUseCase
    ) : ViewModel(), BaseViewModel by BasicViewModel() {

        val mediatorResult = mediatorUseCase.observe()

        private val _useCaseResult = MutableLiveData<Resource<String>>()
        val useCaseResult: LiveData<Resource<String>>
            get() = _useCaseResult

        private val _mediatorUseCaseResult = MutableLiveData<Resource<String>>()
        val mediatorUseCaseResult: LiveData<Resource<String>>
            get() = _mediatorUseCaseResult

        fun executeUseCase() {
            invokeUseCase(
                useCase = useCase,
                params = Unit
            )
        }

        fun executeFailingUseCase() {
            invokeUseCase(
                useCase = failingUseCase,
                params = Unit
            )
        }

        fun executeUseCaseWithResult() {
            invokeUseCase(
                resultData = _useCaseResult,
                useCase = useCase,
                params = Unit
            )
        }

        fun executeMediatorUseCase() {
            invokeMediatorUseCase(
                useCase = mediatorUseCase,
                params = Unit
            )
        }

        fun executeMediatorUseCaseWithResult() {
            invokeMediatorUseCase(
                resultData = _mediatorUseCaseResult,
                useCase = mediatorUseCase,
                params = Unit
            )
        }
    }

    private class TestUseCase : UseCase<Unit, String>() {
        override suspend fun execute(params: Unit): String {
            return RESULT
        }
    }

    private class TestFailingUseCase : UseCase<Unit, String>() {
        override suspend fun execute(params: Unit): String {
            throw RuntimeException()
        }
    }

    private class TestMediatorUseCase : MediatorUseCase<Unit, String>() {
        override fun executeInternal(params: Unit): LiveData<Resource<String>> {
            return liveData {
                emit(Resource.loading(null))
                emit(Resource.success(RESULT))
            }
        }
    }

    companion object {
        private const val RESULT = "result"
    }
}