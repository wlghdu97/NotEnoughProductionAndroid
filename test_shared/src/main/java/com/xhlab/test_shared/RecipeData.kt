package com.xhlab.test_shared

import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.recipes.GregtechRecipe
import com.xhlab.nep.model.recipes.ShapedRecipe
import com.xhlab.nep.model.recipes.ShapelessRecipe

object RecipeData {

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

    // sources -> type -> recipes
    fun getInputStream() = """
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