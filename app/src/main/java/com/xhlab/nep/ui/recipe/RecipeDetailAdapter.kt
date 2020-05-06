package com.xhlab.nep.ui.recipe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.view.isGone
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.recipes.MachineRecipe.Companion.PowerType
import com.xhlab.nep.shared.domain.recipe.model.CraftingRecipeView
import com.xhlab.nep.shared.domain.recipe.model.MachineRecipeView
import com.xhlab.nep.shared.domain.recipe.model.RecipeView
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import java.text.NumberFormat
import java.util.*

class RecipeDetailAdapter(
    private val targetElementId: Long? = null,
    private val listener: ElementListener? = null
) : PagedListAdapter<RecipeView, RecipeDetailAdapter.RecipeDetailViewHolder>(
    object : DiffUtil.ItemCallback<RecipeView>() {
        override fun areItemsTheSame(oldItem: RecipeView, newItem: RecipeView): Boolean {
            return oldItem.recipeId == newItem.recipeId
        }

        override fun areContentsTheSame(oldItem: RecipeView, newItem: RecipeView): Boolean {
            return (oldItem.recipeId == newItem.recipeId &&
                    oldItem.itemList == oldItem.itemList)
        }
    }
) {
    private val integerFormat = NumberFormat.getIntegerInstance(Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(when (viewType) {
                0 -> R.layout.holder_recipe
                1 -> R.layout.holder_recipe_machine
                else -> throw IllegalArgumentException("invalid view type.")
            }, parent, false)
        return RecipeDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CraftingRecipeView -> 0
            is MachineRecipeView -> 1
            else -> throw IllegalArgumentException("invalid recipe view type.")
        }
    }

    inner class RecipeDetailViewHolder(itemView: View) : BindableViewHolder<RecipeView>(itemView) {
        private val elementName: TextView? = itemView.findViewById(R.id.element_name)
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)

        private val gregProperty: TextView? = itemView.findViewById(R.id.greg_properties)

        private val itemList: RecyclerView = itemView.findViewById(R.id.item_list)
        private val itemAdapter by lazy { RecipeElementAdapter(listener) }

        private val byproductList: RecyclerView? = itemView.findViewById(R.id.byproduct_list)
        private val byproductAdapter by lazy { RecipeElementAdapter(listener) }
        private val byproductGroup: Group? = itemView.findViewById(R.id.byproduct_group)

        private val context: Context
            get() = itemView.context

        init {
            itemList.adapter = itemAdapter
            byproductList?.adapter = byproductAdapter
        }

        override fun bindNotNull(model: RecipeView) {
            val targetElement = model.resultItemList.find { it.id == targetElementId }
            if (elementName != null && targetElement != null) {
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
                        PowerType.EU.type -> context.getString(R.string.txt_eu)
                        PowerType.RF.type -> context.getString(R.string.txt_rf)
                        else -> context.getString(R.string.txt_unknown)
                    }
                    val unitTick = "$unit${context.getString(R.string.txt_per_tick)}"
                    val durationSec = model.duration / 20f
                    val total = model.ept.toLong() * model.duration
                    gregProperty?.text = context.formatString(
                        R.string.form_machine_property,
                        integerFormat.format(model.ept),
                        unitTick,
                        integerFormat.format(durationSec),
                        integerFormat.format(total),
                        unit
                    )
                    val byproductList = model.resultItemList.filter { it.id != targetElementId }
                    byproductAdapter.submitList(byproductList)
                    byproductGroup?.isGone = byproductList.isEmpty()
                }
                is CraftingRecipeView -> {
                    // nothing to show more
                    byproductGroup?.isGone = true
                }
            }

            itemAdapter.submitList(model.itemList)
        }
    }
}