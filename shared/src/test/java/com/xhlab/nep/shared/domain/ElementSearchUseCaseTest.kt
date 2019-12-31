package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import com.xhlab.nep.shared.db.view.SearchResultView
import com.xhlab.nep.shared.domain.item.ElementSearchUseCase
import com.xhlab.nep.shared.tests.util.LiveDataTestUtil
import com.xhlab.nep.shared.tests.util.MainCoroutineRule
import com.xhlab.nep.shared.tests.util.asPagedList
import com.xhlab.nep.shared.tests.util.createMockDataSourceFactory
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ElementSearchUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var elementRepo: ElementRepo

    private lateinit var useCase: ElementSearchUseCase

    private val term = "testTerm"

    private val testList = listOf(
        SearchResultView(
            id = 0,
            localizedName = "result1",
            type = ITEM
        ),
        SearchResultView(
            id = 1,
            localizedName = "result2",
            type = ITEM
        )
    )

    @Before
    fun prepare() {
        elementRepo = mock {
            val pagedSearchResultList = createMockDataSourceFactory(testList)
            onBlocking { searchByName(term) }.doReturn(pagedSearchResultList)
        }

        useCase = ElementSearchUseCase(elementRepo)
    }

    @Test
    fun loadPagedListSuccessfully() = runBlocking {
        useCase.execute(term)

        assertEquals(
            Resource.success(testList.asPagedList()),
            LiveDataTestUtil.getValue(useCase.observe())
        )
        verify(elementRepo, times(1)).searchByName(term)

        Unit
    }
}