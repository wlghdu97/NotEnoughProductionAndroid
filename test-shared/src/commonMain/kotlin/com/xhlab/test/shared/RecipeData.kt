package com.xhlab.test.shared

import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.model.form.ItemForm
import com.xhlab.nep.model.form.OreDictForm
import com.xhlab.nep.model.form.ReplacementForm
import com.xhlab.nep.model.form.recipes.*

@Suppress("MagicNumber", "MaxLineLength")
object RecipeData {

    val itemList = listOf(
        ItemForm(1, "gt.bwMetaGeneratedcrushed.5", "Crushed Fluor-Buergerite Ore"),
        ItemForm(1, "gt.bwMetaGeneratedcrushedPurified.5", "Purified Fluor-Buergerite Ore"),
        ItemForm(1, "gt.metaitem.01.17", "Tiny Pile of Sodium Dust"),
        ItemForm(1, "gt.metaitem.01.2299", "Stone Dust"),
        ItemForm(1, "item.crushedAgarditeCd", "Crushed Agardite (Cd) Ore"),
        ItemForm(1, "item.crushedPurifiedAgarditeCd", "Purified Crushed Agardite (Cd) Ore"),
        ItemForm(1, "gt.metaitem.01.35", "Tiny Pile of Copper Dust"),
        ItemForm(1, "item.beeNugget", "Apatite Shard"),
        ItemForm(1, "gt.metaitem.01.2530", "Apatite Dust"),
        ItemForm(1, "gt.metaitem.01.833", "Tiny Pile of Phosphate Dust"),
        ItemForm(1, "block.cobblestone", "Cobblestone"),
        ItemForm(1, "block.furnace", "Furnace"),
        ItemForm(1, "item.coal", "Coal"),
        ItemForm(1, "item.stick", "Stick"),
        ItemForm(4, "item.torch", "Torch"),
        ItemForm(1, "item.projectred.core.part.conductive_plate", "Conductive Plate"),
        ItemForm(1, "item.projectred.core.part.cathode", "Cathode"),
        ItemForm(1, "item.projectred.core.part.plate", "Circuit Plate"),
        ItemForm(1, "item.projectred.integration.gate|26", "Comparator"),
        ItemForm(1, "item.netherquartz", "Nether Quartz"),
        ItemForm(1, "gt.metaitem.01.8516", "Certus Quartz"),
        ItemForm(1, "gt.metaitem.01.8523", "Quartzite"),
        ItemForm(
            1,
            "item.appliedenergistics2.ItemMaterial.PurifiedCertusQuartzCrystal",
            "Pure Certus Quartz Crystal"
        ),
        ItemForm(
            1,
            "item.appliedenergistics2.ItemMaterial.CertusQuartzCrystalCharged",
            "Charged Certus Quartz Crystal"
        ),
        ItemForm(
            1,
            "item.appliedenergistics2.ItemMaterial.PurifiedNetherQuartzCrystal",
            "Pure Nether Quartz Crystal"
        ),
        ItemForm(1, "item.appliedenergistics2.ItemMaterial.FluixCrystal", "Fluix Crystal"),
        ItemForm(1, "gt.blockmachines.wire.steel.02", "2x Steel Wire"),
        ItemForm(2, "gt.blockmachines.wire.steel.01", "1x Steel Wire")
    )

    val fluidList = listOf(
        FluidForm(1000, "fluid.tile.water", "Water")
    )

    val replacementList = listOf(
        ReplacementForm("gemQuartz", listOf(itemList[19])),
        ReplacementForm("crystalQuartz", listOf(itemList[19])),
        ReplacementForm("gemNetherQuartz", listOf(itemList[19])),
        ReplacementForm(
            "craftingQuartz",
            listOf(
                itemList[20],
                itemList[21],
                itemList[22],
                itemList[19],
                itemList[23],
                itemList[24],
                itemList[25]
            )
        ),
        ReplacementForm("itemNetherQuartz", listOf(itemList[19], itemList[24])),
        ReplacementForm("crystalNetherQuartz", listOf(itemList[19])),
        ReplacementForm("wireGt02Steel", listOf(itemList[26]))
    )

    val machineList = listOf(
        Machine(0, "gregtech", "Ore Washing Plant"),
        Machine(1, "gregtech", "Thermal Centrifuge")
    )

    val machineRecipeList = listOf(
        MachineRecipeForm(
            true, 500, 0, 16, 0,
            listOf(itemList[0]),
            listOf(itemList[1], itemList[2], itemList[3]),
            listOf(fluidList[0]),
            listOf()
        ),
        MachineRecipeForm(
            true, 500, 0, 16, 0,
            listOf(itemList[4]),
            listOf(itemList[5], itemList[6], itemList[3]),
            listOf(fluidList[0]),
            listOf()
        ),
        MachineRecipeForm(
            true, 500, 0, 48, 1,
            listOf(itemList[7]),
            listOf(itemList[8], itemList[9], itemList[3]),
            listOf(),
            listOf()
        )
    )

    val shapedRecipeList = listOf(
        ShapedRecipeForm(
            listOf(
                itemList[10], itemList[10], itemList[10],
                itemList[10], /*null*/ itemList[10],
                itemList[10], itemList[10], itemList[10]
            ),
            itemList[11]
        )
    )

    val shapelessRecipeList = listOf(
        ShapelessRecipeForm(
            listOf(
                itemList[12], /*null*/ /*null*/
                itemList[13] /*null*/ /*null*/
                /*null*/ /*null*/ /*null*/
            ),
            itemList[14]
        )
    )

    private val quartzList = OreDictForm(
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
        ShapedOreDictRecipeForm(
            listOf(
                itemList[15], itemList[16], itemList[15],
                quartzList, itemList[15], quartzList,
                itemList[17], itemList[15], itemList[17]
            ), itemList[18]
        )
    )

    val shapelessOreRecipeList = listOf(
        ShapelessOreDictRecipeForm(
            listOf(
                OreDictForm(1, listOf(replacementList[6].oreDictName))
            ), itemList[27]
        )
    )

    // sources -> type -> recipes
    fun getInputStream() = """
        {"sources": [
            {"type": "gregtech",
             "machines": [
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
            {"type": "shapedOre",
             "recipes": [
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
            ]},
            {"type": "shapelessOre",
             "recipes": [
                {"iI":[
                    ["wireGt02Steel"]
                ],
                "o":
                {"a":2,
                "uN":"gt.blockmachines.wire.steel.01",
                "lN":"1x Steel Wire"
                }}
            ]},
            {"type": "replacements",
             "reps": [
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
            ]}
        ]}
    """.trimIndent().byteInputStream()
}
