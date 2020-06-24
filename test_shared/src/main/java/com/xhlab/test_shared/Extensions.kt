package com.xhlab.test_shared

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.model.recipes.MachineRecipe

fun ProcessView.connectMachineRecipe(
    from: MachineRecipe,
    to: MachineRecipe,
    key: Element,
    reversed: Boolean = false
) {
    connectRecipe(
        from = from.toView(),
        to = to.toView(),
        element = key.toView(),
        reversed = reversed
    )
}

fun ProcessView.disconnectMachineRecipe(
    from: MachineRecipe,
    to: MachineRecipe,
    key: Element,
    reversed: Boolean = false
) {
    disconnectRecipe(
        from = from.toView(),
        to = to.toView(),
        element = key.toView(),
        reversed = reversed
    )
}

fun MachineRecipe.toView() = ProcessViewData.MachineRecipeViewImpl(
    recipeId = ProcessViewData.machineRecipeList.indexOf(this).toLong(),
    itemList = getInputs().map { it.toView() },
    resultItemList = getOutput().map { it.toView() },
    isEnabled = isEnabled,
    duration = duration,
    powerType = powerType,
    ept = ept,
    machineId = machineId,
    machineName = ProcessData.machineList.find { it.id == machineId }?.name ?: "Invalid name"
)

fun Element.toView() = ProcessViewData.RecipeElementViewImpl(
    id = ProcessData.elementList.indexOf(this).toLong(),
    amount = amount,
    unlocalizedName = unlocalizedName,
    localizedName = localizedName,
    type = when (this) {
        is Item -> 0
        is Fluid -> 1
        else -> 2
    },
    metaData = if (this is Item) {
        metaData
    } else null
)