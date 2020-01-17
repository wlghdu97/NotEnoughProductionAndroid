package com.xhlab.test_shared

import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.model.oredict.Replacement
import com.xhlab.nep.model.recipes.*

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
        Item(4, "item.torch", "Torch"),
        Item(1, "item.projectred.core.part.conductive_plate", "Conductive Plate"),
        Item(1, "item.projectred.core.part.cathode", "Cathode"),
        Item(1, "item.projectred.core.part.plate", "Circuit Plate"),
        Item(1, "item.projectred.integration.gate|26", "Comparator"),
        Item(1, "item.netherquartz", "Nether Quartz"),
        Item(1, "gt.metaitem.01.8516", "Certus Quartz"),
        Item(1, "gt.metaitem.01.8523", "Quartzite"),
        Item(1, "item.appliedenergistics2.ItemMaterial.PurifiedCertusQuartzCrystal", "Pure Certus Quartz Crystal"),
        Item(1, "item.appliedenergistics2.ItemMaterial.CertusQuartzCrystalCharged", "Charged Certus Quartz Crystal"),
        Item(1, "item.appliedenergistics2.ItemMaterial.PurifiedNetherQuartzCrystal", "Pure Nether Quartz Crystal"),
        Item(1, "item.appliedenergistics2.ItemMaterial.FluixCrystal", "Fluix Crystal"),
        Item(1, "gt.blockmachines.wire.steel.02", "2x Steel Wire"),
        Item(2, "gt.blockmachines.wire.steel.01", "1x Steel Wire")
    )

    val fluidList = listOf(
        Fluid(1000, "fluid.tile.water", "Water")
    )

    val replacementList = listOf(
        Replacement("gemQuartz", listOf(itemList[19])),
        Replacement("crystalQuartz", listOf(itemList[19])),
        Replacement("gemNetherQuartz", listOf(itemList[19])),
        Replacement("craftingQuartz", listOf(itemList[20], itemList[21], itemList[22], itemList[19], itemList[23], itemList[24], itemList[25])),
        Replacement("itemNetherQuartz", listOf(itemList[19], itemList[24])),
        Replacement("crystalNetherQuartz", listOf(itemList[19])),
        Replacement("wireGt02Steel", listOf(itemList[26]))
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
            itemList[10], /*null*/      itemList[10],
            itemList[10], itemList[10], itemList[10]),
            itemList[11]
        )
    )

    val shapelessRecipeList = listOf(
        ShapelessRecipe(listOf(
            itemList[12], /*null*/      /*null*/
            itemList[13]  /*null*/      /*null*/
            /*null*/      /*null*/      /*null*/),
            itemList[14]
        )
    )


    private val quartzList = OreDictElement(
        amount = 1,
        oreDictNameList = listOf(
            replacementList[0].oreDictName,
            replacementList[1].oreDictName,
            replacementList[2].oreDictName,
            replacementList[3].oreDictName,
            replacementList[4].oreDictName,
            replacementList[5].oreDictName
        )
    )

    val shapedOreRecipeList = listOf(
        ShapedOreDictRecipe(listOf(
            itemList[15], itemList[16], itemList[15],
            quartzList,   itemList[15], quartzList,
            itemList[17], itemList[15], itemList[17]
            ), itemList[18]
        )
    )

    val shapelessOreRecipeList = listOf(
        ShapelessOreDictRecipe(listOf(
            OreDictElement(1, listOf(replacementList[6].oreDictName))
        ), itemList[27])
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
            ]},
            {"recipes": [
                {"iI":[
                {"a":1,
                "uN":"item.projectred.core.part.conductive_plate",
                "lN":"Conductive Plate"
                },
                {"a":1,
                "uN":"item.projectred.core.part.cathode",
                "lN":"Cathode"
                },
                {"a":1,
                "uN":"item.projectred.core.part.conductive_plate",
                "lN":"Conductive Plate"
                },
                ["gemQuartz","crystalQuartz","gemNetherQuartz","craftingQuartz","itemNetherQuartz","crystalNetherQuartz"],
                {"a":1,
                "uN":"item.projectred.core.part.conductive_plate",
                "lN":"Conductive Plate"
                },
                ["gemQuartz","crystalQuartz","gemNetherQuartz","craftingQuartz","itemNetherQuartz","crystalNetherQuartz"],
                {"a":1,
                "uN":"item.projectred.core.part.plate",
                "lN":"Circuit Plate"
                },
                {"a":1,
                "uN":"item.projectred.core.part.conductive_plate",
                "lN":"Conductive Plate"
                },
                {"a":1,
                "uN":"item.projectred.core.part.plate",
                "lN":"Circuit Plate"
                }],
                "o":
                {"a":1,
                "uN":"item.projectred.integration.gate|26",
                "lN":"Comparator"
                }}
            ], "type": "shapedOre"},
            {"recipes": [
                {"iI":[
                    ["wireGt02Steel"]
                ],
                "o":
                {"a":2,
                "uN":"gt.blockmachines.wire.steel.01",
                "lN":"1x Steel Wire"
                }}
            ], "type": "shapelessOre"},
            {"reps": [
                {"name":"gemQuartz",
                "reps":[
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                }]},
                {"name":"crystalQuartz",
                "reps":[
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                }]},
                {"name":"craftingQuartz",
                "reps":[
                    {"a":1,
                    "uN":"gt.metaitem.01.8516",
                    "lN":"Certus Quartz"
                    },
                    {"a":1,
                    "uN":"gt.metaitem.01.8523",
                    "lN":"Quartzite"
                    },
                    {"a":1,
                    "uN":"item.appliedenergistics2.ItemMaterial.PurifiedCertusQuartzCrystal",
                    "lN":"Pure Certus Quartz Crystal"
                    },
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                    },
                    {"a":1,
                    "uN":"item.appliedenergistics2.ItemMaterial.CertusQuartzCrystalCharged",
                    "lN":"Charged Certus Quartz Crystal"
                    },
                    {"a":1,
                    "uN":"item.appliedenergistics2.ItemMaterial.PurifiedNetherQuartzCrystal",
                    "lN":"Pure Nether Quartz Crystal"
                    },
                    {"a":1,
                    "uN":"item.appliedenergistics2.ItemMaterial.FluixCrystal",
                    "lN":"Fluix Crystal"
                }]},
                {"name":"gemNetherQuartz",
                "reps":[
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                }]},
                {"name":"itemNetherQuartz",
                "reps":[
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                    },
                    {"a":1,
                    "uN":"item.appliedenergistics2.ItemMaterial.PurifiedNetherQuartzCrystal",
                    "lN":"Pure Nether Quartz Crystal"
                }]},
                {"name":"crystalNetherQuartz",
                "reps":[
                    {"a":1,
                    "uN":"item.netherquartz",
                    "lN":"Nether Quartz"
                }]},
                {"name":"wireGt02Steel",
                "reps":[
                    {"a":1,
                    "uN":"gt.blockmachines.wire.steel.02",
                    "lN":"2x Steel Wire"
                }]}
            ], "type": "replacements"}
        ]}
    """.trimIndent().byteInputStream()
}