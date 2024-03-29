package com.xhlab.test.shared

import com.xhlab.nep.model.Machine
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView.Companion.PowerType.EU
import com.xhlab.nep.model.recipes.view.MachineRecipeView.Companion.PowerType.NONE
import kotlinx.serialization.Serializable

@Suppress("MagicNumber", "MaxLineLength")
object ProcessData {
    val itemList = listOf(
        RecipeElementImpl(0, 1, "tile.stonebrick", "Cobblestone", 0),
        RecipeElementImpl(1, 1, "tile.sand.default", "Sand", 0),
        RecipeElementImpl(2, 1, "tile.gravel", "Gravel", 0),
        RecipeElementImpl(3, 3, "tile.gravel", "Gravel", 0),
        RecipeElementImpl(4, 1, "item.flint", "Flint", 0),
        RecipeElementImpl(5, 1, "item.SandDust", "Quartz Sand", 0),
        RecipeElementImpl(6, 1, "gt.metaitem.01.802", "Tiny Pile of Flint Dust", 0),
        RecipeElementImpl(7, 4, "gt.metaitem.01.802", "Tiny Pile of Flint Dust", 0),
        RecipeElementImpl(8, 1, "gt.metaitem.01.2890", "Glass Dust", 0),
        RecipeElementImpl(9, 1, "tile.glass", "Glass", 0),

        RecipeElementImpl(10, 1, "gt.metaitem.01.30677", "Ethylene Cell", 0),
        RecipeElementImpl(11, 1, "gt.metaitem.01.30706", "Ethanol Cell", 0),
        RecipeElementImpl(12, 1, "tile.sapling.oak", "Sapling", 0),
        RecipeElementImpl(13, 1, "gt.metaitem.01.30460", "Hydrogen Sulfide Cell", 0),
        RecipeElementImpl(14, 1, "gt.metaitem.01.2022", "Sulfur Dust", 0),
        RecipeElementImpl(15, 1, "gt.metaitem.01.30001", "Hydrogen Cell", 0),
        RecipeElementImpl(16, 1, "ic2.itemCellEmpty", "Empty Cell", 0),
        RecipeElementImpl(17, 1, "gt.integrated_circuit", "Programmed Circuit", 0, "1"),
        RecipeElementImpl(18, 1, "gt.integrated_circuit", "Programmed Circuit", 0, "3"),

        RecipeElementImpl(19, 1, "tile.chest", "Chest", 0),
        RecipeElementImpl(20, 4, "logWood", "Ore Chain", 3),
        RecipeElementImpl(21, 4, "plankWood", "Ore Chain", 3),

        RecipeElementImpl(22, 1, "tile.log.oak", "Oak Wood", 0),
        RecipeElementImpl(23, 4, "tile.wood.oak", "Oak Wood Planks", 0),

        RecipeElementImpl(24, 1, "gt.metaitem.01.17874", "Polyethylene Sheet", 0),
        RecipeElementImpl(25, 0, "gt.metaitem.01.32301", "Mold (Plate)", 0)
    )

    val fluidList = listOf(
        RecipeElementImpl(19, 1500, "fluid.molten.plastic", "Molten Polyethylene", 0),
        RecipeElementImpl(20, 7000, "fluid.oxygen", "Oxygen Gas", 0),
        RecipeElementImpl(21, 500, "fluid.oxygen", "Oxygen Gas", 0),
        RecipeElementImpl(22, 1000, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementImpl(23, 40, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementImpl(24, 750, "fluid.dilutedsulfuricacid", "Diluted Sulfuric Acid", 0),
        RecipeElementImpl(25, 1000, "fluid.sulfuricacid", "Sulfuric Acid", 0),
        RecipeElementImpl(26, 20, "fluid.sulfuricacid", "Sulfuric Acid", 0),
        RecipeElementImpl(27, 1000, "fluid.bioethanol", "Ethanol", 0),
        RecipeElementImpl(28, 20, "fluid.bioethanol", "Ethanol", 0),
        RecipeElementImpl(29, 40, "ic2.fluidBiomass", "Biomass", 0),
        RecipeElementImpl(30, 100, "ic2.fluidBiomass", "Biomass", 0),
        RecipeElementImpl(31, 1000, "fluid.tile.water", "Water", 0),
        RecipeElementImpl(32, 1500, "fluid.tile.water", "Water", 0),
        RecipeElementImpl(33, 1000, "fluid.liquid_hydricsulfur", "Hydrogen Sulfide", 0),
        RecipeElementImpl(34, 2000, "fluid.hydrogen", "Hydrogen Gas", 0),
        RecipeElementImpl(35, 1000, "fluid.hydrogen", "Hydrogen Gas", 0),
        RecipeElementImpl(36, 5, "fluid.tile.water", "Water", 1),
        RecipeElementImpl(37, 144, "fluid.molten.plastic", "Molten Polyethylene", 0)
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
        Machine(8, "gregtech", "Electrolyzer"),
        Machine(9, "gregtech", "Cutting Machine"),
        Machine(10, "gregtech", "Fluid Solidifier")
    )

    val recipeList = listOf(
        MachineRecipeViewImpl(
            0,
            true,
            10,
            EU.type,
            16,
            0,
            machineList[0].name,
            listOf(itemList[0]),
            listOf(itemList[2])
        ),
        MachineRecipeViewImpl(
            1,
            true,
            10,
            EU.type,
            16,
            0,
            machineList[0].name,
            listOf(itemList[2]),
            listOf(itemList[1])
        ),
        MachineRecipeViewImpl(
            2,
            true,
            0,
            NONE.type,
            0,
            3,
            machineList[3].name,
            listOf(itemList[3]),
            listOf(itemList[4])
        ),
        MachineRecipeViewImpl(
            3,
            true,
            200,
            EU.type,
            8,
            1,
            machineList[1].name,
            listOf(itemList[1]),
            listOf(itemList[5])
        ),
        MachineRecipeViewImpl(
            4,
            true,
            400,
            EU.type,
            2,
            1,
            machineList[1].name,
            listOf(itemList[4]),
            listOf(itemList[7])
        ),
        MachineRecipeViewImpl(
            5,
            true,
            0,
            NONE.type,
            0,
            3,
            machineList[3].name,
            listOf(itemList[5], itemList[6]),
            listOf(itemList[8])
        ),
        MachineRecipeViewImpl(
            6,
            true,
            200,
            EU.type,
            16,
            2,
            machineList[2].name,
            listOf(itemList[8]),
            listOf(itemList[9])
        ),

        MachineRecipeViewImpl(
            7,
            true,
            1120,
            EU.type,
            30,
            4,
            machineList[4].name,
            listOf(itemList[10], itemList[17], fluidList[1]),
            listOf(itemList[16], fluidList[0])
        ),
        MachineRecipeViewImpl(
            8,
            true,
            1200,
            EU.type,
            120,
            4,
            machineList[4].name,
            listOf(itemList[11], itemList[17], fluidList[6]),
            listOf(itemList[10], fluidList[3])
        ),
        MachineRecipeViewImpl(
            9,
            true,
            16,
            EU.type,
            1,
            5,
            machineList[5].name,
            listOf(itemList[16], fluidList[8]),
            listOf(itemList[11])
        ),
        MachineRecipeViewImpl(
            10,
            true,
            16,
            EU.type,
            24,
            6,
            machineList[6].name,
            listOf(itemList[17], fluidList[10]),
            listOf(fluidList[9])
        ),
        MachineRecipeViewImpl(
            11,
            true,
            800,
            EU.type,
            3,
            7,
            machineList[7].name,
            listOf(itemList[12], fluidList[12]),
            listOf(fluidList[11])
        ),
        MachineRecipeViewImpl(
            12,
            true,
            30,
            EU.type,
            30,
            6,
            machineList[6].name,
            listOf(itemList[17], fluidList[4]),
            listOf(fluidList[7])
        ),
        MachineRecipeViewImpl(
            13,
            true,
            60,
            EU.type,
            30,
            4,
            machineList[4].name,
            listOf(itemList[13], fluidList[12]),
            listOf(itemList[16], fluidList[5])
        ),
        MachineRecipeViewImpl(
            14,
            true,
            16,
            EU.type,
            1,
            5,
            machineList[5].name,
            listOf(itemList[16], fluidList[14]),
            listOf(itemList[13])
        ),
        MachineRecipeViewImpl(
            15,
            true,
            60,
            EU.type,
            8,
            4,
            machineList[4].name,
            listOf(itemList[14], itemList[17], fluidList[15]),
            listOf(fluidList[14])
        ),
        MachineRecipeViewImpl(
            16,
            true,
            16,
            EU.type,
            1,
            5,
            machineList[5].name,
            listOf(itemList[15]),
            listOf(itemList[16], fluidList[16])
        ),
        MachineRecipeViewImpl(
            17,
            true,
            2000,
            EU.type,
            30,
            8,
            machineList[8].name,
            listOf(itemList[16], itemList[18], fluidList[13]),
            listOf(itemList[15], fluidList[2])
        ),

        CraftingRecipeViewImpl(
            18,
            listOf(itemList[4], itemList[20], itemList[21]),
            listOf(itemList[19])
        ),
        MachineRecipeViewImpl(
            19,
            true,
            400,
            EU.type,
            7,
            9,
            machineList[9].name,
            listOf(itemList[22], fluidList[17]),
            listOf(itemList[23])
        ),

        MachineRecipeViewImpl(
            20,
            true,
            40,
            EU.type,
            7,
            10,
            machineList[10].name,
            listOf(itemList[25], fluidList[18]),
            listOf(itemList[24])
        )
    )

    val supplierRecipeList = listOf(
        SupplierRecipe(fluidList[1]),
        SupplierRecipe(itemList[22])
    )

    val oreChainRecipeList = listOf(
        OreChainRecipe(itemList[20], itemList[22]),
        OreChainRecipe(itemList[21], itemList[23])
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
            connectRecipe(supplierRecipeList[0], recipeList[7], fluidList[1])
        }

    val processChest: Process
        get() = Process("process03", "Chest process", recipeList[18], itemList[19]).apply {
            connectRecipe(oreChainRecipeList[0], recipeList[18], itemList[20])
            connectRecipe(supplierRecipeList[1], oreChainRecipeList[0], itemList[22])
            connectRecipe(oreChainRecipeList[1], recipeList[18], itemList[21])
            connectRecipe(recipeList[19], oreChainRecipeList[1], itemList[23])
        }

    val processPlasticSheet: Process
        get() = Process(
            "process04",
            "Polyethylene sheet process",
            recipeList[20],
            itemList[24]
        ).apply {
            markNotConsumed(recipeList[20], itemList[25])
            connectProcess(processPE, recipeList[20], fluidList[18])
        }

    val processList = listOf(processGlass, processPE, processChest, processPlasticSheet)

    val processSummaryList = processList.map {
        ProcessSummaryImpl(
            processId = it.id,
            name = it.name,
            unlocalizedName = it.targetOutput.unlocalizedName,
            localizedName = it.targetOutput.localizedName,
            amount = it.targetOutput.amount,
            nodeCount = it.getRecipeNodeCount()
        )
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
            ),
            RecipeNode(
                supplierRecipeList[0],
                listOf()
            )
        )
    )

    @Serializable
    data class MachineRecipeViewImpl(
        override val recipeId: Long,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String,
        override val itemList: List<RecipeElementImpl>,
        override val resultItemList: List<RecipeElementImpl>
    ) : MachineRecipeView()

    @Serializable
    data class CraftingRecipeViewImpl(
        override val recipeId: Long,
        override val itemList: List<RecipeElementImpl>,
        override val resultItemList: List<RecipeElementImpl>
    ) : CraftingRecipeView()

    @Serializable
    data class RecipeElementImpl(
        override val id: Long,
        override val amount: Int,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val type: Int,
        override val metaData: String? = null
    ) : RecipeElement()

    data class ProcessSummaryImpl(
        override val processId: String,
        override val name: String,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val amount: Int,
        override val nodeCount: Int
    ) : ProcessSummary()
}
