package com.xhlab.test_shared

import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType.EU
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType.NONE
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeElementView

object ProcessData {
    val itemList = listOf(
        RecipeElementViewImpl(0, 1, "tile.stonebrick", "Cobblestone", 0),
        RecipeElementViewImpl(1, 1, "tile.sand.default", "Sand", 0),
        RecipeElementViewImpl(2, 1, "tile.gravel", "Gravel", 0),
        RecipeElementViewImpl(3, 3, "tile.gravel", "Gravel", 0),
        RecipeElementViewImpl(4, 1, "item.flint", "Flint", 0),
        RecipeElementViewImpl(5, 1, "item.SandDust", "Quartz Sand", 0),
        RecipeElementViewImpl(6, 1, "gt.metaitem.01.802", "Tiny Pile of Flint Dust", 0),
        RecipeElementViewImpl(7, 4, "gt.metaitem.01.802", "Tiny Pile of Flint Dust", 0),
        RecipeElementViewImpl(8, 1, "gt.metaitem.01.2890", "Glass Dust", 0),
        RecipeElementViewImpl(9, 1, "tile.glass", "Glass", 0),

        RecipeElementViewImpl(10, 1, "gt.metaitem.01.30677", "Ethylene Cell", 0),
        RecipeElementViewImpl(11, 1, "gt.metaitem.01.30706", "Ethanol Cell", 0),
        RecipeElementViewImpl(12, 1, "tile.sapling.oak", "Sapling", 0),
        RecipeElementViewImpl(13, 1, "gt.metaitem.01.30460", "Hydrogen Sulfide Cell", 0),
        RecipeElementViewImpl(14, 1, "gt.metaitem.01.2022", "Sulfur Dust", 0),
        RecipeElementViewImpl(15, 1, "gt.metaitem.01.30001", "Hydrogen Cell", 0),
        RecipeElementViewImpl(16, 1, "ic2.itemCellEmpty", "Empty Cell", 0),
        RecipeElementViewImpl(17, 1, "gt.integrated_circuit", "Programmed Circuit", 0, "1"),
        RecipeElementViewImpl(18, 1, "gt.integrated_circuit", "Programmed Circuit", 0, "3")
    )

    val fluidList = listOf(
        RecipeElementViewImpl(19, 1500, "fluid.molten.plastic", "Molten Polyethylene", 0),
        RecipeElementViewImpl(20, 7000, "fluid.oxygen", "Oxygen Gas", 0),
        RecipeElementViewImpl(21, 500, "fluid.oxygen", "Oxygen Gas", 0),
        RecipeElementViewImpl(22, 1000, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementViewImpl(23, 40, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementViewImpl(24, 750, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementViewImpl(25, 1000, "fluid.sulfuricacid", "Sulfuric Acid", 0),
        RecipeElementViewImpl(26, 20, "fluid.sulfuricacid", "Sulfuric Acid", 0),
        RecipeElementViewImpl(27, 1000, "fluid.bioethanol", "Ethanol", 0),
        RecipeElementViewImpl(28, 20, "fluid.bioethanol", "Ethanol", 0),
        RecipeElementViewImpl(29, 40, "ic2.fluidBiomass", "Biomass", 0),
        RecipeElementViewImpl(30, 100, "ic2.fluidBiomass", "Biomass", 0),
        RecipeElementViewImpl(31, 1000, "fluid.tile.water", "Water", 0),
        RecipeElementViewImpl(32, 1500, "fluid.tile.water", "Water", 0),
        RecipeElementViewImpl(33, 1000, "fluid.liquid_hydricsulfur", "Hydrogen Sulfide", 0),
        RecipeElementViewImpl(34, 2000, "fluid.hydrogen", "Hydrogen Gas", 0),
        RecipeElementViewImpl(35, 1000, "fluid.hydrogen", "Hydrogen Gas", 0)
    )

    val elementList = itemList + fluidList

    val machineList = listOf(
        Machine(0, "gregtech", "Forge Hammer"),
        Machine(1, "gregtech", "Macerator"),
        Machine(2, "gregtech", "Alloy Smelter"),
        Machine(3, "vanilla", "Crafting Table"),

        Machine(4, "gregtech", "Chemical Reactor"),
        Machine(5, "gregtech", "Fluid Canner"),
        Machine(6, "gregtech", "Distillery"),
        Machine(7, "gregtech", "Brewing Machine"),
        Machine(8, "gregtech", "Electrolyzer")
    )

    val recipeList = listOf(
        MachineRecipeViewImpl(0, true, 10, EU.type, 16, 0, machineList[0].name, listOf(itemList[0]), listOf(itemList[2])),
        MachineRecipeViewImpl(1, true, 10, EU.type, 16, 0, machineList[0].name, listOf(itemList[2]), listOf(itemList[1])),
        MachineRecipeViewImpl(2, true, 0, NONE.type, 0, 3, machineList[3].name, listOf(itemList[3]), listOf(itemList[4])),
        MachineRecipeViewImpl(3, true, 200, EU.type, 8, 1, machineList[1].name, listOf(itemList[1]), listOf(itemList[5])),
        MachineRecipeViewImpl(4, true, 400, EU.type, 2, 1, machineList[1].name, listOf(itemList[4]), listOf(itemList[7])),
        MachineRecipeViewImpl(5, true, 0, NONE.type, 0, 3, machineList[3].name, listOf(itemList[5], itemList[6]), listOf(itemList[8])),
        MachineRecipeViewImpl(6, true, 200, EU.type, 16, 2, machineList[2].name, listOf(itemList[8]), listOf(itemList[9])),

        MachineRecipeViewImpl(7, true, 1120, EU.type, 30, 4, machineList[4].name, listOf(itemList[10], itemList[17], fluidList[1]), listOf(itemList[16], fluidList[0])),
        MachineRecipeViewImpl(8, true, 1200, EU.type, 120, 4, machineList[4].name, listOf(itemList[11], itemList[17], fluidList[6]), listOf(itemList[10], fluidList[3])),
        MachineRecipeViewImpl(9, true, 16, EU.type, 1, 5, machineList[5].name, listOf(itemList[16], fluidList[8]), listOf(itemList[11])),
        MachineRecipeViewImpl(10, true, 16, EU.type, 24, 6, machineList[6].name, listOf(itemList[17], fluidList[10]), listOf(fluidList[9])),
        MachineRecipeViewImpl(11, true, 800, EU.type, 3, 7, machineList[7].name, listOf(itemList[12], fluidList[12]), listOf(fluidList[11])),
        MachineRecipeViewImpl(12, true, 30, EU.type, 30, 6, machineList[6].name, listOf(itemList[17], fluidList[4]), listOf(fluidList[7])),
        MachineRecipeViewImpl(13, true, 60, EU.type, 30, 4, machineList[4].name, listOf(itemList[13], fluidList[12]), listOf(itemList[16], fluidList[5])),
        MachineRecipeViewImpl(14, true, 16, EU.type, 1, 5, machineList[5].name, listOf(itemList[16], fluidList[14]), listOf(itemList[13])),
        MachineRecipeViewImpl(15, true, 60, EU.type, 8, 4, machineList[4].name, listOf(itemList[14], itemList[17], fluidList[15]), listOf(fluidList[14])),
        MachineRecipeViewImpl(16, true, 16, EU.type, 1, 5, machineList[5].name, listOf(itemList[15]), listOf(itemList[16], fluidList[16])),
        MachineRecipeViewImpl(17, true, 2000, EU.type, 30, 8, machineList[8].name, listOf(itemList[16], itemList[18], fluidList[13]), listOf(itemList[15], fluidList[2]))
    )

    val processGlass: Process
        get() = Process("process01", "Primitive glass forge", recipeList[6], itemList[9]).apply {
            connectRecipe(recipeList[5], recipeList[6], itemList[8])
            connectRecipe(recipeList[4], recipeList[5], itemList[6])
            connectRecipe(recipeList[3], recipeList[5], itemList[5])
            connectRecipe(recipeList[2], recipeList[4], itemList[4])
            connectRecipe(recipeList[1], recipeList[3], itemList[1])
            connectRecipe(recipeList[0], recipeList[2], itemList[2])
            connectRecipe(recipeList[0], recipeList[1], itemList[2])
        }

    val processPE: Process
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

    val processList = listOf(processGlass, processPE)

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

    data class MachineRecipeViewImpl(
        override val recipeId: Long,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String,
        override val itemList: List<RecipeElementView>,
        override val resultItemList: List<RecipeElementView>
    ) : MachineRecipeView()

    data class RecipeElementViewImpl(
        override val id: Long,
        override val amount: Int,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val type: Int,
        override val metaData: String? = null
    ) : RecipeElementView()
}