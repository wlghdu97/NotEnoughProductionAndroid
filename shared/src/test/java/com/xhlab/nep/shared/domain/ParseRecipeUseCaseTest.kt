package com.xhlab.nep.shared.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
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
        val result = useCase.observe()

        useCase.execute(getInputStream())
        // wait till parsing is done
        await().until { LiveDataTestUtil.getValue(result)?.status == Resource.Status.SUCCESS }

        assertEquals(
            Resource.Status.SUCCESS,
            LiveDataTestUtil.getValue(result)?.status
        )

        verify(elementRepo, times(1)).deleteAll()
        verify(generalPreference, times(1)).setDBLoaded(true)
    }

    private class FakeRecipeRepo : RecipeRepo {
        override suspend fun insertRecipes(recipes: List<Recipe>) {
            for (recipe in recipes) {
                assertTrue(when (recipe) {
                    is GregtechRecipe -> RecipeList.gregRecipeList.contains(recipe)
                    is ShapedRecipe -> RecipeList.shapedRecipeList.contains(recipe)
                    is ShapelessRecipe -> RecipeList.shapelessRecipeList.contains(recipe)
                    else -> false
                })
            }
        }
    }

    private class FakeGregtechRepo : GregtechRepo {
        override suspend fun insertGregtechMachine(machineName: String): Int {
            return RecipeList.gregMachineMap[machineName] ?: -1
        }

        override suspend fun deleteGregtechMachines() {
            // does nothing
        }
    }

    private object RecipeList {

        val itemList = listOf(
            Item(1, "gt.bwMetaGeneratedcrushed.5", "Crushed Fluor-Buergerite Ore"),
            Item(1, "gt.bwMetaGeneratedcrushedPurified.5", "Purified Fluor-Buergerite Ore"),
            Item(1, "gt.metaitem.01.17", "Tiny Pile of Sodium Dust"),
            Item(1, "gt.metaitem.01.2299", "Stone Dust"),
            Item(1, "item.crushedAgarditeCd", "Crushed Agardite (Cd) Ore"),
            Item(1, "item.crushedPurifiedAgarditeCd", "Purified Crushed Agardite (Cd) Ore"),
            Item(1, "gt.metaitem.01.35", "Tiny Pile of Copper Dust"),
            Item(1, "item.beeNugget", "Apatite Shard"),
            Item(1, "gt.metaitem.01.2530", "Apatite Dust"),
            Item(1, "gt.metaitem.01.833", "Tiny Pile of Phosphate Dust"),
            Item(1, "block.cobblestone", "Cobblestone"),
            Item(1, "block.furnace", "Furnace"),
            Item(1, "item.coal", "Coal"),
            Item(1, "item.stick", "Stick"),
            Item(4, "item.torch", "Torch")
        )

        val fluidList = listOf(
            Fluid(1000, "fluid.tile.water", "Water")
        )

        val gregMachineMap = mapOf(
            "Ore Washing Plant" to 0,
            "Thermal Centrifuge" to 1
        )

        val gregRecipeList = listOf(
            GregtechRecipe(
                true, 500, 16, 0,
                listOf(itemList[0]),
                listOf(itemList[1], itemList[2], itemList[3]),
                listOf(fluidList[0]),
                listOf()),
            GregtechRecipe(
                true, 500, 16, 0,
                listOf(itemList[4]),
                listOf(itemList[5], itemList[6], itemList[3]),
                listOf(fluidList[0]),
                listOf()
            ),
            GregtechRecipe(
                true, 500, 48, 1,
                listOf(itemList[7]),
                listOf(itemList[8], itemList[9], itemList[3]),
                listOf(),
                listOf()
            )
        )

        val shapedRecipeList = listOf(
            ShapedRecipe(listOf(
                itemList[10], itemList[10], itemList[10],
                itemList[10], null, itemList[10],
                itemList[10], itemList[10], itemList[10]),
                itemList[11]
            )
        )

        val shapelessRecipeList = listOf(
            ShapelessRecipe(listOf(
                itemList[12], null, null,
                itemList[13], null, null,
                null, null, null),
                itemList[14]
            )
        )
    }

    // sources -> type -> recipes
    private fun getInputStream() = """
        {"sources": [
            {"machines": [
                {"n": "Ore Washing Plant", 
                 "recs": [
                    {"en":true, "dur":500, "eut":16, 
                    "iI":[
                        {"a":1, 
                        "uN": "gt.bwMetaGeneratedcrushed.5", 
                        "lN": "Crushed Fluor-Buergerite Ore"}
                    ],
                    "iO":[
                        {"a":1,
                        "uN":"gt.bwMetaGeneratedcrushedPurified.5",
                        "lN":"Purified Fluor-Buergerite Ore"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.17",
                        "lN":"Tiny Pile of Sodium Dust"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.2299",
                        "lN":"Stone Dust"
                        }
                    ],
                    "fI":[
                        {"a":1000,
                        "uN":"fluid.tile.water",
                        "lN":"Water"
                        }
                    ],
                    "fO":[]
                    },
                    {"en":true, "dur":500, "eut":16,
                    "iI":[
                        {"a":1,
                        "uN":"item.crushedAgarditeCd",
                        "lN":"Crushed Agardite (Cd) Ore"
                        }
                    ],
                    "iO":[
                        {"a":1,
                        "uN":"item.crushedPurifiedAgarditeCd",
                        "lN":"Purified Crushed Agardite (Cd) Ore"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.35",
                        "lN":"Tiny Pile of Copper Dust"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.2299",
                        "lN":"Stone Dust"
                        }
                    ],
                    "fI":[
                        {"a":1000,
                        "uN":"fluid.tile.water",
                        "lN":"Water"
                        }
                    ],
                    "fO":[]
                    }
                ]},
                {"n":"Thermal Centrifuge",
                "recs":[
                    {"en":true, "dur":500, "eut":48,
                    "iI":[
                        {"a":1,
                        "uN":"item.beeNugget",
                        "lN":"Apatite Shard"
                        }
                    ],
                    "iO":[
                        {"a":1,
                        "uN":"gt.metaitem.01.2530",
                        "lN":"Apatite Dust"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.833",
                        "lN":"Tiny Pile of Phosphate Dust"
                        },
                        {"a":1,
                        "uN":"gt.metaitem.01.2299",
                        "lN":"Stone Dust"
                        }
                    ],
                    "fI":[],
                    "fO":[]
                    }
                ]}
            ]},
            {"type": "shaped",
            "recs": [
                {"iI": [
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                null,
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                },
                {"a":1,
                "uN":"block.cobblestone",
                "lN":"Cobblestone"
                }
                ],
                "o": 
                {"a":1,
                "uN":"block.furnace",
                "lN":"Furnace"
                }}
            ]},
            {"type": "shapeless",
            "recs": [
                {"iI":[
                {"a":1,
                "uN":"item.coal",
                "lN":"Coal"
                },
                null,
                null,
                {"a":1,
                "uN":"item.stick",
                "lN":"Stick"
                },
                null,
                null,
                null,
                null,
                null
                ],
                "o":
                {"a":4,
                "uN":"item.torch",
                "lN":"Torch"
                }}
            ]}
        ]}
    """.trimIndent().byteInputStream()
}