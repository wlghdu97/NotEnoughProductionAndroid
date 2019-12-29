package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.gregtech.GregtechRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.parser.GregtechRecipeParser
import com.xhlab.nep.shared.parser.ShapedRecipeParser
import com.xhlab.nep.shared.parser.ShapelessRecipeParser
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.tests.util.LiveDataTestUtil
import com.xhlab.nep.shared.tests.util.MainCoroutineRule
import com.xhlab.nep.shared.util.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ParseRecipeUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    @ExperimentalCoroutinesApi
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var elementRepo: ElementRepo
    private lateinit var recipeRepo: RecipeRepo
    private lateinit var gregtechRepo: GregtechRepo

    private lateinit var generalPreference: GeneralPreference

    private lateinit var useCase: ParseRecipeUseCase

    @Before
    fun prepare() {
        elementRepo = mock {
            onBlocking { deleteAll() }.doReturn(Unit)
        }
        recipeRepo = mock {
            onBlocking { insertRecipes(any()) }.doReturn(Unit)
        }
        gregtechRepo = mock {
            onBlocking { insertGregtechMachine(any()) }.doReturn(0)
            onBlocking { deleteGregtechMachines() }.doReturn(Unit)
        }

        val gregtechRecipeParser = GregtechRecipeParser(
            itemParser = ItemParser(),
            fluidParser = FluidParser(),
            recipeRepo = recipeRepo,
            gregtechRepo = gregtechRepo
        )

        val shapedRecipeParser = ShapedRecipeParser(VanillaItemParser(), recipeRepo)

        val shapelessRecipeParser = ShapelessRecipeParser(VanillaItemParser(), recipeRepo)

        generalPreference = mock {
            doNothing().`when`(it).setDBLoaded(true)
        }

        useCase = ParseRecipeUseCase(
            gregtechRecipeParser = gregtechRecipeParser,
            shapedRecipeParser = shapedRecipeParser,
            shapelessRecipeParser = shapelessRecipeParser,
            elementRepo = elementRepo,
            gregtechRepo = gregtechRepo,
            generalPreference = generalPreference
        )
    }

    @Test
    fun executeSuccessfully() = runBlocking {
        val result = useCase.invoke(getNormalInputStream())

        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(result)?.status
        )

        verify(elementRepo, times(1)).deleteAll()
        verify(gregtechRepo, times(1)).deleteGregtechMachines()
        verify(generalPreference, times(1)).setDBLoaded(true)
    }

    @Test
    fun executeFailed() {
        val result = useCase.invoke(getMalformedInputStream())

        assertEquals(
            Resource.Status.ERROR,
            LiveDataTestUtil.getValue(result)?.status
        )
    }

    // sources -> type -> recipes
    private fun getNormalInputStream() = """
        {"sources": [{"machines": [{"n": "Ore Washing Plant", "recs": []}]}]}
    """.trimIndent().byteInputStream()

    private fun getMalformedInputStream() = """
        {"sources": 
    """.trimIndent().byteInputStream()
}