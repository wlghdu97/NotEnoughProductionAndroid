package com.xhlab.test_shared

import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType.EU
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType.NONE

object ProcessData {
    val itemList = listOf(
        /*0*/Item(1, "tile.stonebrick", "Cobblestone"),
        /*1*/Item(1, "tile.sand.default", "Sand"),
        /*2*/Item(1, "tile.gravel", "Gravel"),
        /*3*/Item(3, "tile.gravel", "Gravel"),
        /*4*/Item(1, "item.flint", "Flint"),
        /*5*/Item(1, "item.SandDust", "Quartz Sand"),
        /*6*/Item(1, "gt.metaitem.01.802", "Tiny Pile of Flint Dust"),
        /*7*/Item(4, "gt.metaitem.01.802", "Tiny Pile of Flint Dust"),
        /*8*/Item(1, "gt.metaitem.01.2890", "Glass Dust"),
        /*9*/Item(1, "tile.glass", "Glass"),

        /*10*/Item(1, "gt.metaitem.01.30677", "Ethylene Cell"),
        /*11*/Item(1, "gt.metaitem.01.30706", "Ethanol Cell"),
        /*12*/Item(1, "tile.sapling.oak", "Sapling"),
        /*13*/Item(1, "gt.metaitem.01.30460", "Hydrogen Sulfide Cell"),
        /*14*/Item(1, "gt.metaitem.01.2022", "Sulfur Dust"),
        /*15*/Item(1, "gt.metaitem.01.30001", "Hydrogen Cell"),
        /*16*/Item(1, "ic2.itemCellEmpty", "Empty Cell"),
        /*17*/Item(1, "gt.integrated_circuit", "Programmed Circuit", "1"),
        /*18*/Item(1, "gt.integrated_circuit", "Programmed Circuit", "3")
    )

    val fluidList = listOf(
        /*0*/Fluid(1500, "fluid.molten.plastic", "Molten Polyethylene"),
        /*1*/Fluid(7000, "fluid.oxygen", "Oxygen Gas"),
        /*2*/Fluid(500, "fluid.oxygen", "Oxygen Gas"),
        /*3*/Fluid(1000, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid"),
        /*4*/Fluid(40, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid"),
        /*5*/Fluid(750, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid"),
        /*6*/Fluid(1000, "fluid.sulfuricacid", "Sulfuric Acid"),
        /*7*/Fluid(20, "fluid.sulfuricacid", "Sulfuric Acid"),
        /*8*/Fluid(1000, "fluid.bioethanol", "Ethanol"),
        /*9*/Fluid(20, "fluid.bioethanol", "Ethanol"),
        /*10*/Fluid(40, "ic2.fluidBiomass", "Biomass"),
        /*11*/Fluid(100, "ic2.fluidBiomass", "Biomass"),
        /*12*/Fluid(1000, "fluid.tile.water", "Water"),
        /*13*/Fluid(1500, "fluid.tile.water", "Water"),
        /*14*/Fluid(1000, "fluid.liquid_hydricsulfur", "Hydrogen Sulfide"),
        /*15*/Fluid(2000, "fluid.hydrogen", "Hydrogen Gas"),
        /*16*/Fluid(1000, "fluid.hydrogen", "Hydrogen Gas")
    )

    val elementList = itemList + fluidList

    val machineList = listOf(
        /*0*/Machine(0, "gregtech", "Forge Hammer"),
        /*1*/Machine(1, "gregtech", "Macerator"),
        /*2*/Machine(2, "gregtech", "Alloy Smelter"),
        /*3*/Machine(3, "vanilla", "Crafting Table"),

        /*4*/Machine(4, "gregtech", "Chemical Reactor"),
        /*5*/Machine(5, "gregtech", "Fluid Canner"),
        /*6*/Machine(6, "gregtech", "Distillery"),
        /*7*/Machine(7, "gregtech", "Brewing Machine"),
        /*8*/Machine(8, "gregtech", "Electrolyzer")
    )

    val recipeList = listOf<Recipe>(
        /*0*/MachineRecipe(true, 10, EU.type, 16, 0, listOf(itemList[0]), listOf(itemList[2]), listOf(), listOf()),
        /*1*/MachineRecipe(true, 10, EU.type, 16, 0, listOf(itemList[2]), listOf(itemList[1]), listOf(), listOf()),
        /*2*/MachineRecipe(true, 0, NONE.type, 0, 3, listOf(itemList[3]), listOf(itemList[4]), listOf(), listOf()),
        /*3*/MachineRecipe(true, 200, EU.type, 8, 1, listOf(itemList[1]), listOf(itemList[5]), listOf(), listOf()),
        /*4*/MachineRecipe(true, 400, EU.type, 2, 1, listOf(itemList[4]), listOf(itemList[7]), listOf(), listOf()),
        /*5*/MachineRecipe(true, 0, NONE.type, 0, 3, listOf(itemList[5], itemList[6]), listOf(itemList[8]), listOf(), listOf()),
        /*6*/MachineRecipe(true, 200, EU.type, 16, 2, listOf(itemList[8]), listOf(itemList[9]), listOf(), listOf()),

        /*7*/MachineRecipe(true, 1120, EU.type, 30, 4, listOf(itemList[10], itemList[17]), listOf(itemList[16]), listOf(fluidList[1]), listOf(fluidList[0])),
        /*8*/MachineRecipe(true, 1200, EU.type, 120, 4, listOf(itemList[11], itemList[17]), listOf(itemList[10]), listOf(fluidList[6]), listOf(fluidList[3])),
        /*9*/MachineRecipe(true, 16, EU.type, 1, 5, listOf(itemList[16]), listOf(itemList[11]), listOf(fluidList[8]), listOf()),
        /*10*/MachineRecipe(true, 16, EU.type, 24, 6, listOf(itemList[17]), listOf(), listOf(fluidList[10]), listOf(fluidList[9])),
        /*11*/MachineRecipe(true, 800, EU.type, 3, 7, listOf(itemList[12]), listOf(), listOf(fluidList[12]), listOf(fluidList[11])),
        /*12*/MachineRecipe(true, 30, EU.type, 30, 6, listOf(itemList[17]), listOf(), listOf(fluidList[4]), listOf(fluidList[7])),
        /*13*/MachineRecipe(true, 60, EU.type, 30, 4, listOf(itemList[13]), listOf(itemList[16]), listOf(fluidList[12]), listOf(fluidList[5])),
        /*14*/MachineRecipe(true, 16, EU.type, 1, 5, listOf(itemList[16]), listOf(itemList[13]), listOf(fluidList[14]), listOf()),
        /*15*/MachineRecipe(true, 60, EU.type, 8, 4, listOf(itemList[14], itemList[17]), listOf(), listOf(fluidList[15]), listOf(fluidList[14])),
        /*16*/MachineRecipe(true, 16, EU.type, 1, 5, listOf(itemList[15]), listOf(itemList[16]), listOf(), listOf(fluidList[16])),
        /*17*/MachineRecipe(true, 2000, EU.type, 30, 8, listOf(itemList[16], itemList[18]), listOf(itemList[15]), listOf(fluidList[13]), listOf(fluidList[2]))
    )

    val processGlass: Process<Recipe>
        get() = Process("process01", "Primitive glass forge", recipeList[6], itemList[9]).apply {
            connectRecipe(recipeList[5], recipeList[6], itemList[8])
            connectRecipe(recipeList[4], recipeList[5], itemList[6])
            connectRecipe(recipeList[3], recipeList[5], itemList[5])
            connectRecipe(recipeList[2], recipeList[4], itemList[4])
            connectRecipe(recipeList[1], recipeList[3], itemList[1])
            connectRecipe(recipeList[0], recipeList[2], itemList[2])
            connectRecipe(recipeList[0], recipeList[1], itemList[2])
        }

    val processPE: Process<Recipe>
        get() = Process("process02", "Polyethylene process", recipeList[7], fluidList[0]).apply {
            connectRecipe(recipeList[7], recipeList[9], itemList[16], true)
            connectRecipe(recipeList[8], recipeList[7], itemList[10])
            connectRecipe(recipeList[8], recipeList[12], fluidList[3], true)
            connectRecipe(recipeList[9], recipeList[8], itemList[11])
            connectRecipe(recipeList[10], recipeList[9], fluidList[9])
            connectRecipe(recipeList[11], recipeList[10], fluidList[11])
            connectRecipe(recipeList[12], recipeList[8], fluidList[7])
            connectRecipe(recipeList[13], recipeList[12], fluidList[5])
            connectRecipe(recipeList[13], recipeList[14], itemList[16], true)
            connectRecipe(recipeList[14], recipeList[13], itemList[13])
            connectRecipe(recipeList[15], recipeList[14], fluidList[14])
            connectRecipe(recipeList[16], recipeList[15], fluidList[16])
            connectRecipe(recipeList[16], recipeList[17], itemList[16], true)
            connectRecipe(recipeList[17], recipeList[16], itemList[15])
            connectRecipe(recipeList[17], recipeList[7], fluidList[2])
            markNotConsumed(recipeList[7], itemList[17])
            markNotConsumed(recipeList[8], itemList[17])
            markNotConsumed(recipeList[10], itemList[17])
            markNotConsumed(recipeList[12], itemList[17])
            markNotConsumed(recipeList[15], itemList[17])
            markNotConsumed(recipeList[17], itemList[18])
        }

    val processGlassTree = RecipeNode(
        recipeList[6],
        listOf(
            RecipeNode(
                recipeList[5],
                listOf(
                    RecipeNode(
                        recipeList[4],
                        listOf(
                            RecipeNode(
                                recipeList[2],
                                listOf(
                                    RecipeNode(
                                        recipeList[0],
                                        listOf()
                                    )
                                )
                            )
                        )
                    ),
                    RecipeNode(
                        recipeList[3],
                        listOf(
                            RecipeNode(
                                recipeList[1],
                                listOf(
                                    RecipeNode(
                                        recipeList[0],
                                        listOf()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )

    val processPETree = RecipeNode(
        recipeList[7],
        listOf(
            RecipeNode(
                recipeList[8],
                listOf(
                    RecipeNode(
                        recipeList[9],
                        listOf(
                            RecipeNode(
                                recipeList[10],
                                listOf(
                                    RecipeNode(
                                        recipeList[11],
                                        listOf()
                                    )
                                )
                            )
                        )
                    ),
                    RecipeNode(
                        recipeList[12],
                        listOf(
                            RecipeNode(
                                recipeList[13],
                                listOf(
                                    RecipeNode(
                                        recipeList[14],
                                        listOf(
                                            RecipeNode(
                                                recipeList[15],
                                                listOf(
                                                    RecipeNode(
                                                        recipeList[16],
                                                        listOf(
                                                            RecipeNode(
                                                                recipeList[17],
                                                                listOf()
                                                            )
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}