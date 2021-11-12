package com.xhlab.nep.model.form.recipes

import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.model.form.ItemForm

data class MachineRecipeForm(
    val isEnabled: Boolean,
    val duration: Int,
    val powerType: Int,
    val ept: Int, // electricity per tick
    val machineId: Int,
    private val itemInputs: List<ItemForm>,
    private val itemOutputs: List<ItemForm>,
    private val fluidInputs: List<FluidForm>,
    private val fluidOutputs: List<FluidForm>
) : RecipeForm {

    override fun getInputs(): List<ElementForm> {
        val inputs = arrayListOf<ElementForm>()
        inputs.addAll(itemInputs)
        inputs.addAll(fluidInputs)
        return inputs
    }

    override fun getOutput(): List<ElementForm> {
        val outputs = arrayListOf<ElementForm>()
        outputs.addAll(itemOutputs)
        outputs.addAll(fluidOutputs)
        return outputs
    }
}
