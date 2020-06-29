package com.xhlab.test_shared

import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.model.recipes.view.RecipeView

object ProcessViewData {

    val machineRecipeList = ProcessData.recipeList.map { it as MachineRecipe }

    val processGlass: ProcessView
        get() = ProcessView(ProcessData.processGlass.id, ProcessData.processGlass.name, machineRecipeList[6].toView(), ProcessData.itemList[9].toView()).apply {
            connectMachineRecipe(machineRecipeList[5], machineRecipeList[6], ProcessData.itemList[8])
            connectMachineRecipe(machineRecipeList[4], machineRecipeList[5], ProcessData.itemList[6])
            connectMachineRecipe(machineRecipeList[3], machineRecipeList[5], ProcessData.itemList[5])
            connectMachineRecipe(machineRecipeList[2], machineRecipeList[4], ProcessData.itemList[4])
            connectMachineRecipe(machineRecipeList[1], machineRecipeList[3], ProcessData.itemList[1])
            connectMachineRecipe(machineRecipeList[0], machineRecipeList[2], ProcessData.itemList[2])
            connectMachineRecipe(machineRecipeList[0], machineRecipeList[1], ProcessData.itemList[2])
        }

    val processPE: ProcessView
        get() = ProcessView(ProcessData.processPE.id, ProcessData.processPE.name, machineRecipeList[7].toView(), ProcessData.fluidList[0].toView()).apply {
            connectMachineRecipe(machineRecipeList[7], machineRecipeList[9], ProcessData.itemList[16], true)
            connectMachineRecipe(machineRecipeList[8], machineRecipeList[7], ProcessData.itemList[10])
            connectMachineRecipe(machineRecipeList[8], machineRecipeList[12], ProcessData.fluidList[3], true)
            connectMachineRecipe(machineRecipeList[9], machineRecipeList[8], ProcessData.itemList[11])
            connectMachineRecipe(machineRecipeList[10], machineRecipeList[9], ProcessData.fluidList[9])
            connectMachineRecipe(machineRecipeList[11], machineRecipeList[10], ProcessData.fluidList[11])
            connectMachineRecipe(machineRecipeList[12], machineRecipeList[8], ProcessData.fluidList[7])
            connectMachineRecipe(machineRecipeList[13], machineRecipeList[12], ProcessData.fluidList[5])
            connectMachineRecipe(machineRecipeList[13], machineRecipeList[14], ProcessData.itemList[16], true)
            connectMachineRecipe(machineRecipeList[14], machineRecipeList[13], ProcessData.itemList[13])
            connectMachineRecipe(machineRecipeList[15], machineRecipeList[14], ProcessData.fluidList[14])
            connectMachineRecipe(machineRecipeList[16], machineRecipeList[15], ProcessData.fluidList[16])
            connectMachineRecipe(machineRecipeList[16], machineRecipeList[17], ProcessData.itemList[16], true)
            connectMachineRecipe(machineRecipeList[17], machineRecipeList[16], ProcessData.itemList[15])
            connectMachineRecipe(machineRecipeList[17], machineRecipeList[7], ProcessData.fluidList[2])
            markNotConsumed(machineRecipeList[7], ProcessData.itemList[17])
            markNotConsumed(machineRecipeList[8], ProcessData.itemList[17])
            markNotConsumed(machineRecipeList[10], ProcessData.itemList[17])
            markNotConsumed(machineRecipeList[12], ProcessData.itemList[17])
            markNotConsumed(machineRecipeList[15], ProcessData.itemList[17])
            markNotConsumed(machineRecipeList[17], ProcessData.itemList[18])
        }

    val processViewList = listOf(processGlass, processPE)

    val processGlassTree = RecipeNode(
        machineRecipeList[6].toView(),
        listOf(
            RecipeNode(
                machineRecipeList[5].toView(),
                listOf(
                    RecipeNode(
                        machineRecipeList[4].toView(),
                        listOf(
                            RecipeNode(
                                machineRecipeList[2].toView(),
                                listOf(
                                    RecipeNode<RecipeView>(
                                        machineRecipeList[0].toView(),
                                        listOf()
                                    )
                                )
                            )
                        )
                    ),
                    RecipeNode(
                        machineRecipeList[3].toView(),
                        listOf(
                            RecipeNode(
                                machineRecipeList[1].toView(),
                                listOf(
                                    RecipeNode<RecipeView>(
                                        machineRecipeList[0].toView(),
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
        machineRecipeList[7].toView(),
        listOf(
            RecipeNode(
                machineRecipeList[8].toView(),
                listOf(
                    RecipeNode(
                        machineRecipeList[9].toView(),
                        listOf(
                            RecipeNode(
                                machineRecipeList[10].toView(),
                                listOf(
                                    RecipeNode<RecipeView>(
                                        machineRecipeList[11].toView(),
                                        listOf()
                                    )
                                )
                            )
                        )
                    ),
                    RecipeNode(
                        machineRecipeList[12].toView(),
                        listOf(
                            RecipeNode(
                                machineRecipeList[13].toView(),
                                listOf(
                                    RecipeNode(
                                        machineRecipeList[14].toView(),
                                        listOf(
                                            RecipeNode(
                                                machineRecipeList[15].toView(),
                                                listOf(
                                                    RecipeNode(
                                                        machineRecipeList[16].toView(),
                                                        listOf(
                                                            RecipeNode<RecipeView>(
                                                                machineRecipeList[17].toView(),
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
        override val itemList: List<RecipeElementView>,
        override val resultItemList: List<RecipeElementView>,
        override val isEnabled: Boolean,
        override val duration: Int,
        override val powerType: Int,
        override val ept: Int,
        override val machineId: Int,
        override val machineName: String
    ) : MachineRecipeView()

    data class RecipeElementViewImpl(
        override val id: Long,
        override val amount: Int,
        override val unlocalizedName: String,
        override val localizedName: String,
        override val type: Int,
        override val metaData: String?
    ) : RecipeElementView()
}