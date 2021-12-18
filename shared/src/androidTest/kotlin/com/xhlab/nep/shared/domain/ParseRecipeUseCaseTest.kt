package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.form.ReplacementForm
import com.xhlab.nep.model.form.recipes.*
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.shared.data.element.ElementRepo
import com.xhlab.nep.shared.data.machine.MachineRepo
import com.xhlab.nep.shared.data.machinerecipe.MachineRecipeRepo
import com.xhlab.nep.shared.data.oredict.OreDictRepo
import com.xhlab.nep.shared.data.recipe.RecipeRepo
import com.xhlab.nep.shared.domain.parser.ParseRecipeUseCase
import com.xhlab.nep.shared.parser.*
import com.xhlab.nep.shared.parser.element.FluidParser
import com.xhlab.nep.shared.parser.element.ItemParser
import com.xhlab.nep.shared.parser.element.VanillaItemParser
import com.xhlab.nep.shared.parser.oredict.OreDictItemParser
import com.xhlab.nep.shared.parser.oredict.ReplacementParser
import com.xhlab.nep.shared.parser.stream.JsonReader
import com.xhlab.nep.shared.preference.GeneralPreference
import com.xhlab.nep.shared.util.StringResolver
import com.xhlab.nep.shared.util.dropLoading
import com.xhlab.nep.shared.util.runBlockingTest
import com.xhlab.test.shared.RecipeData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kr.sparkweb.multiplatform.paging.Pager
import kr.sparkweb.multiplatform.util.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ParseRecipeUseCaseTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var elementRepo: ElementRepo
    private lateinit var recipeRepo: RecipeRepo
    private lateinit var machineRepo: MachineRepo
    private lateinit var machineRecipeRepo: MachineRecipeRepo
    private lateinit var oreDictRepo: OreDictRepo

    private lateinit var generalPreference: GeneralPreference
    private lateinit var stringResolver: StringResolver

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

        stringResolver = mock {
            // ignore meta data
            doReturn("").`when`(it).formatString(any(), any())
        }

        val machineRecipeParser = MachineRecipeParser(
            itemParser = ItemParser(stringResolver),
            fluidParser = FluidParser(),
            machineRepo = machineRepo,
            recipeRepo = recipeRepo
        )

        val shapedRecipeParser = ShapedRecipeParser(VanillaItemParser(), recipeRepo)
        val shapelessRecipeParser = ShapelessRecipeParser(VanillaItemParser(), recipeRepo)
        val shapedOreRecipeParser = ShapedOreRecipeParser(OreDictItemParser(), recipeRepo)
        val shapelessOreRecipeParser = ShapelessOreRecipeParser(OreDictItemParser(), recipeRepo)
        val replacementListParser = ReplacementListParser(ReplacementParser(), oreDictRepo)
        val furnaceRecipeParser =
            FurnaceRecipeParser(ItemParser(stringResolver), machineRepo, recipeRepo)

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
            furnaceRecipeParser = furnaceRecipeParser,
            elementRepo = elementRepo,
            machineRepo = machineRepo,
            generalPreference = generalPreference,
            io = Dispatchers.Main
        )
    }

    @Test
    fun executeSuccessfully() = runBlockingTest {
        val result = useCase.observe()

        val reader = { JsonReader(RecipeData.getInputStream().bufferedReader()) }
        useCase.execute(Dispatchers.Main, reader)

        assertEquals(
            Resource.Status.SUCCESS,
            result.dropLoading().first().status
        )

        verify(elementRepo, times(1)).deleteAll()
        verify(generalPreference, times(2)).setDBLoaded(any())
    }

    // TODO: revive this
//    @Test
//    fun cancelUseCase() = runBlockingTest {
//        val result = useCase.observe()
//
//        useCase.execute(Dispatchers.Main, RecipeData.getInputStream()).apply {
//            cancel()
//        }
//
//        assertEquals(
//            Resource.success("job canceled."),
//            result.dropLoading().first()
//        )
//    }

    private class FakeRecipeRepo : RecipeRepo {
        override suspend fun insertRecipes(recipes: List<RecipeForm>) {
            for (recipe in recipes) {
                assertTrue(
                    when (recipe) {
                        is MachineRecipeForm -> RecipeData.machineRecipeList.contains(recipe)
                        is ShapedRecipeForm -> RecipeData.shapedRecipeList.contains(recipe)
                        is ShapelessRecipeForm -> RecipeData.shapelessRecipeList.contains(recipe)
                        is ShapedOreDictRecipeForm -> RecipeData.shapedOreRecipeList.contains(recipe)
                        is ShapelessOreDictRecipeForm -> RecipeData.shapelessOreRecipeList.contains(
                            recipe
                        )
                        else -> false
                    }
                )
            }
        }

        override suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElement> {
            TODO("not implemented")
        }

        override suspend fun searchRecipeByElement(
            elementId: Long,
            term: String
        ): Pager<Int, RecipeView> {
            TODO("not implemented")
        }

        override suspend fun searchUsageRecipeByElement(
            elementId: Long,
            term: String
        ): Pager<Int, RecipeView> {
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

        override fun getMachines(): Pager<Int, Machine> {
            TODO("not implemented")
        }
    }

    private class FakeMachineRecipeRepo : MachineRecipeRepo {
        override suspend fun getElementListByRecipe(recipeId: Long): List<RecipeElement> {
            TODO("not implemented")
        }

        override suspend fun searchRecipeByElement(
            elementId: Long,
            machineId: Int,
            term: String
        ): Pager<Int, RecipeView> {
            TODO("not implemented")
        }

        override suspend fun searchUsageRecipeByElement(
            elementId: Long,
            machineId: Int,
            term: String
        ): Pager<Int, RecipeView> {
            TODO("not implemented")
        }
    }

    private class FakeOreDictRepo : OreDictRepo {
        override suspend fun insertReplacements(list: List<ReplacementForm>) {
            for (replacement in list) {
                assertTrue(RecipeData.replacementList.contains(replacement))
            }
        }
    }
}
