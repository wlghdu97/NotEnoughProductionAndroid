package com.xhlab.nep.ui.process.adapters

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import java.text.NumberFormat
import kotlin.math.min

abstract class ProcessTreeViewHolder(itemView: View)
    : BindableViewHolder<RecipeViewDegreeNode>(itemView) {

    private val label: ImageView = itemView.findViewById(R.id.degree_label)
    private val machineName: TextView = itemView.findViewById(R.id.machine_name)
    private val degree: TextView = itemView.findViewById(R.id.degree)
    private val properties: TextView = itemView.findViewById(R.id.recipe_properties)
    protected val elementList: RecyclerView = itemView.findViewById(R.id.element_list)

    protected val context: Context
        get() = itemView.context

    override fun bindNotNull(model: RecipeViewDegreeNode) {
        val recipe = model.node.recipe
        machineName.text = when (recipe) {
            is MachineRecipeView -> recipe.machineName
            is CraftingRecipeView -> context.getString(R.string.txt_crafting_table)
            is SupplierRecipe -> context.getString(R.string.txt_supplier)
            is OreChainRecipe -> context.getString(R.string.txt_ore_chain_recipe)
            else -> context.getString(R.string.txt_unnamed)
        }

        degree.text = context.formatString(R.string.form_degree, model.degree)

        if (recipe is MachineRecipeView) {
            val unit = when (recipe.powerType) {
                MachineRecipe.Companion.PowerType.EU.type -> context.getString(R.string.txt_eu)
                MachineRecipe.Companion.PowerType.RF.type -> context.getString(R.string.txt_rf)
                else -> context.getString(R.string.txt_unknown)
            }
            val unitTick = "$unit${context.getString(R.string.txt_per_tick)}"
            val durationSec = recipe.duration / 20f
            val total = recipe.ept.toLong() * recipe.duration
            properties.isGone = false
            properties.text = context.formatString(
                R.string.form_machine_property,
                integerFormat.format(recipe.ept),
                unitTick,
                integerFormat.format(durationSec),
                integerFormat.format(total),
                unit
            )
        } else {
            properties.isGone = true
        }

        val colorList = getDegreeColorList(context)
        val index = min(colorList.size - 1, model.degree)
        label.setImageDrawable(ColorDrawable(colorList[index]))
    }

    companion object {
        private val integerFormat = NumberFormat.getIntegerInstance()
        private var degreeColors: IntArray? = null

        private fun getDegreeColorList(context: Context): IntArray {
            if (degreeColors == null) {
                degreeColors = context.resources.getIntArray(R.array.degreeColorList)
            }
            return degreeColors!!
        }
    }
}