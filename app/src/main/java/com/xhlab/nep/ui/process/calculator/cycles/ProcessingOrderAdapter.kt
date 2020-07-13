package com.xhlab.nep.ui.process.calculator.cycles

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.OreChainRecipe
import com.xhlab.nep.model.process.SupplierRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import org.jetbrains.anko.layoutInflater
import java.text.DecimalFormat

typealias RecipeRatio = Pair<Recipe, Double>

class ProcessingOrderAdapter
    : RecyclerView.Adapter<ProcessingOrderAdapter.ProcessingOrderViewHolder>() {

    private val recipeList = arrayListOf<RecipeRatio>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessingOrderViewHolder {
        val view = parent.context.layoutInflater
            .inflate(R.layout.holder_processing_order, parent, false)
        return ProcessingOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProcessingOrderViewHolder, position: Int) {
        holder.bind(recipeList[position])
    }

    override fun getItemCount() = recipeList.size

    fun submitRecipeRatioList(list: List<RecipeRatio>) {
        recipeList.clear()
        recipeList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ProcessingOrderViewHolder(itemView: View)
        : BindableViewHolder<RecipeRatio>(itemView) {
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val ratio: TextView = itemView.findViewById(R.id.ratio)

        private val format = DecimalFormat("#.##")

        private val context: Context
            get() = itemView.context

        override fun bindNotNull(model: RecipeRatio) {
            val (recipe, ratio) = model
            machineName.text = when (recipe) {
                is MachineRecipeView -> recipe.machineName
                is CraftingRecipeView -> context.getString(R.string.txt_crafting_table)
                else -> context.getString(R.string.txt_unnamed)
            }
            this.ratio.text = context.formatString(
                R.string.form_ratio,
                format.format(ratio)
            )
        }
    }
}