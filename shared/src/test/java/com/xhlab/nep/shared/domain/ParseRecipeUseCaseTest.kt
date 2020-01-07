package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.recipes.GregtechRecipe
import com.xhlab.nep.model.recipes.ShapedRecipe
import com.xhlab.nep.model.recipes.ShapelessRecipe
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
import com.xhlab.test_shared.RecipeData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.awaitility.Awaitility.await
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
        recipeRepo = FakeRecipeRepo()
        gregtechRepo = FakeGregtechRepo()

        val gregtechRecipeParser = GregtechRecipeParser(
            itemParser = ItemParser(),
            fluidParser = FluidParser(),
            recipeRepo = recipeRepo,
            gregtechRepo = gregtechRepo
        )

        val shapedRecipeParser = ShapedRecipeParser(VanillaItemParser(), recipeRepo)

        val shapelessRecipeParser = ShapelessRecipeParser(VanillaItemParser(), recipeRepo)

        generalPreference = mock {
            doNothing().`when`(it).setDBLoaded(any())
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
        val result = useCase.observe()

        useCase.execute(RecipeData.getInputStream())
        // wait till parsing is done
        await().until { LiveDataTestUtil.getValue(result)?.status == Resource.Status.SUCCESS }

        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(result)?.status
        )

        verify(elementRepo, times(1)).deleteAll()
        verify(generalPreference, times(2)).setDBLoaded(any())
    }

    @Test
    fun cancelUseCase() {
        val result = useCase.observe()

        useCase.execute(RecipeData.getInputStream())
        useCase.cancel()

        assertEquals(
            Resource.success("job canceled."),
            LiveDataTestUtil.getValue(result)
        )
    }

    private class FakeRecipeRepo : RecipeRepo {
        override suspend fun insertRecipes(recipes: List<Recipe>) {
            for (recipe in recipes) {
                assertTrue(when (recipe) {
                    is GregtechRecipe -> RecipeData.gregRecipeList.contains(recipe)
                    is ShapedRecipe -> RecipeData.shapedRecipeList.contains(recipe)
                    is ShapelessRecipe -> RecipeData.shapelessRecipeList.contains(recipe)
                    else -> false
                })
            }
        }
    }

    private class FakeGregtechRepo : GregtechRepo {
        override suspend fun insertGregtechMachine(machineName: String): Int {
            return RecipeData.gregMachineMap[machineName] ?: -1
        }

        override suspend fun deleteGregtechMachines() {
            // does nothing
        }


    }
}