package com.xhlab.nep.ui.adapters

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import java.text.NumberFormat
import java.util.*

abstract class RecipeDetailViewHolder(itemView: View) : BindableViewHolder<RecipeView>(itemView) {

    private val integerFormat = NumberFormat.getIntegerInstance(Locale.getDefault())

    private val icon: ImageView = itemView.findViewById(R.id.icon)
    private val elementName: TextView? = itemView.findViewById(R.id.element_name)
    private val machineName: TextView = itemView.findViewById(R.id.machine_name)

    private val machineProperties: TextView? = itemView.findViewById(R.id.machine_properties)

    private val itemList: RecyclerView = itemView.findViewById(R.id.item_list)
    protected abstract val itemAdapter: RecipeElementAdapter

    private val byproductList: RecyclerView? = itemView.findViewById(R.id.byproduct_list)
    protected abstract val byproductAdapter: RecipeElementAdapter
    private val byproductGroup: Group? = itemView.findViewById(R.id.byproduct_group)

    abstract val targetElementId: Long?
    abstract val isIconVisible: Boolean

    private val context: Context
        get() = itemView.context

    override fun bindNotNull(model: RecipeView) {
        if (itemList.adapter == null) {
            itemList.adapter = itemAdapter
        }
        if (byproductList?.adapter == null) {
            byproductList?.adapter = byproductAdapter
        }
        icon.isGone = !isIconVisible

        val targetElement = model.resultItemList.find { it.id == targetElementId }
        if (elementName != null && targetElement != null) {
            if (isIconVisible) {
                icon.setIcon(targetElement.unlocalizedName)
            }
            elementName.text = context.formatString(
                R.string.form_item_with_amount,
                integerFormat.format(targetElement.amount),
                when (targetElement.localizedName.isEmpty()) {
                    true -> context.getString(R.string.txt_unnamed)
                    false -> targetElement.localizedName.trim()
                }
            )
        }

        machineName.text = when (model) {
            is MachineRecipeView -> model.machineName
            is CraftingRecipeView -> context.getString(R.string.txt_crafting_table)
            else -> context.getString(R.string.txt_unknown_recipe)
        }

        when (model) {
            is MachineRecipeView -> {
                val unit = when (model.powerType) {
                    MachineRecipe.Companion.PowerType.EU.type -> context.getString(R.string.txt_eu)
                    MachineRecipe.Companion.PowerType.RF.type -> context.getString(R.string.txt_rf)
                    MachineRecipe.Companion.PowerType.FUEL.type -> context.getString(R.string.txt_fuel)
                    else -> context.getString(R.string.txt_unknown)
                }
                val unitTick = "$unit${context.getString(R.string.txt_per_tick)}"
                val durationSec = model.duration / 20f
                val total = model.ept.toLong() * model.duration
                machineProperties?.text = context.formatString(
                    R.string.form_machine_property,
                    integerFormat.format(model.ept),
                    unitTick,
                    integerFormat.format(durationSec),
                    integerFormat.format(total),
                    unit
                )
                val byproductList = model.resultItemList.filter { it.id != targetElementId }
                byproductAdapter.submitList(byproductList)
                byproductAdapter.setIconVisibility(isIconVisible)
                byproductGroup?.isGone = byproductList.isEmpty()
            }
            is CraftingRecipeView -> {
                // nothing to show more
                byproductGroup?.isGone = true
            }
        }

        itemAdapter.submitList(model.itemList)
        itemAdapter.setIconVisibility(isIconVisible)
    }
}