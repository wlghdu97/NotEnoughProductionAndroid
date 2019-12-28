package com.xhlab.nep.model.recipes

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.Recipe

data class GregtechRecipe(
    val isEnabled: Boolean,
    val duration: Int,
    val eut: Int,
    val machineId: Int,
    private val itemInputs: List<Item>,
    private val itemOutputs: List<Item>,
    private val fluidInputs: List<Fluid>,
    private val fluidOutputs: List<Fluid>
) : Recipe {

    override fun getInputs(): List<Element?> {
        val inputs = arrayListOf<Element>()
        inputs.addAll(itemInputs)
        inputs.addAll(fluidInputs)
        return inputs
    }

    override fun getOutput(): List<Element> {
        val outputs = arrayListOf<Element>()
        outputs.addAll(itemOutputs)
        outputs.addAll(fluidOutputs)
        return outputs
    }
}