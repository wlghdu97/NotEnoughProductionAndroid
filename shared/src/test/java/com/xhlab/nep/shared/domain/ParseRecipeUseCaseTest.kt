package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.DataSource
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.oredict.Replacement
import com.xhlab.nep.model.recipes.*
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.parser.*
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import com.xhlab.nep.shared.parser.oredict.OreDictItemParser
import com.xhlab.nep.shared.parser.oredict.ReplacementParser
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
    private lateinit var machineRepo: MachineRepo
    private lateinit var machineRecipeRepo: MachineRecipeRepo
    private lateinit var oreDictRepo: OreDictRepo

    private lateinit var generalPreference: GeneralPreference

    private lateinit var useCase: ParseRecipeUseCase

    @Before
    fun prepare() {
        elementRepo = mock {
            onBlocking { deleteAll() }.doReturn(Unit)
        }
        recipeRepo = FakeRecipeRepo()
        machineRepo = FakeMachineRepo()
        machineRecipeRepo = FakeMachineRecipeRepo()
        oreDictRepo = FakeOreDictRepo()

        val machineRecipeParser = MachineRecipeParser(
            itemParser = ItemParser(),
            fluidParser = FluidParser(),
            machineRepo = machineRepo,
            recipeRepo = recipeRepo
        )

        val shapedRecipeParser = ShapedRecipeParser(VanillaItemParser(), recipeRepo)

        val shapelessRecipeParser = ShapelessRecipeParser(VanillaItemParser(), recipeRepo)

        val shapedOreRecipeParser = ShapedOreRecipeParser(OreDictItemParser(), recipeRepo)

        val shapelessOreRecipeParser = ShapelessOreRecipeParser(OreDictItemParser(), recipeRepo)

        val replacementListParser = ReplacementListParser(ReplacementParser(), oreDictRepo)

        generalPreference = mock {
            doNothing().`when`(it).setDBLoaded(any())
        }

        useCase = ParseRecipeUseCase(
            machineRecipeParser = machineRecipeParser,
            shapedRecipeParser = shapedRecipeParser,
            shapelessRecipeParser = shapelessRecipeParser,
            shapedOreRecipeParser = shapedOreRecipeParser,
            shapelessOreRecipeParser = shapelessOreRecipeParser,
            replacementListParser = replacementListParser,
            elementRepo = elementRepo,
            machineRepo = machineRepo,
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
                    is MachineRecipe -> RecipeData.machineRecipeList.contains(recipe)
                    is ShapedRecipe -> RecipeData.shapedRecipeList.contains(recipe)
                    is ShapelessRecipe -> RecipeData.shapelessRecipeList.contains(recipe)
                    is ShapedOreDictRecipe -> RecipeData.shapedOreRecipeList.contains(recipe)
                    is ShapelessOreDictRecipe -> RecipeData.shapelessOreRecipeList.contains(recipe)
                    else -> false
                })
            }
        }

        override suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView> {
            TODO("not implemented")
        }

        override fun searchRecipeByElement(elementId: Long): DataSource.Factory<Int, RecipeView> {
            TODO("not implemented")
        }

        override fun searchUsageRecipeByElement(elementId: Long): DataSource.Factory<Int, RecipeView> {
            TODO("not implemented")
        }
    }

    private class FakeMachineRepo : MachineRepo {
        override suspend fun getMachine(machineId: Int): Machine? {
            return RecipeData.machineList.find { it.id == machineId }
        }

        override suspend fun insertMachine(modName: String, machineName: String): Int {
            return RecipeData.machineList.find {
                it.modName == modName && it.name == machineName
            }?.id ?: -1
        }

        override suspend fun deleteAll() {
            // does nothing
        }

        override fun getMachines(): DataSource.Factory<Int, Machine> {
            TODO("not implemented")
        }
    }

    private class FakeMachineRecipeRepo : MachineRecipeRepo {
        override suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElementView> {
            TODO("not implemented")
        }

        override fun searchRecipeByElement(
            elementId: Long,
            machineId: Int
        ): DataSource.Factory<Int, RecipeView> {
            TODO("not implemented")
        }

        override fun searchUsageRecipeByElement(
            elementId: Long,
            machineId: Int
        ): DataSource.Factory<Int, RecipeView> {
            TODO("not implemented")
        }
    }

    private class FakeOreDictRepo : OreDictRepo {
        override suspend fun insertReplacements(list: List<Replacement>) {
            for (replacement in list) {
                assertTrue(RecipeData.replacementList.contains(replacement))
            }
        }
    }
}