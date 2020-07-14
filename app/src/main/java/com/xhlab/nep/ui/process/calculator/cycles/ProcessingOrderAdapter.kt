package com.xhlab.nep.ui.process.calculator.cycles

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.*
import com.xhlab.nep.model.process.recipes.OreChainRecipe
import com.xhlab.nep.model.process.recipes.ProcessRecipe
import com.xhlab.nep.model.process.recipes.SupplierRecipe
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import org.jetbrains.anko.layoutInflater
import java.text.DecimalFormat
import kotlin.math.min

typealias RecipeRatio = Pair<Recipe, Double>

class ProcessingOrderAdapter
    : RecyclerView.Adapter<ProcessingOrderAdapter.ProcessingOrderViewHolder>() {

    private val recipeList = arrayListOf<RecipeRatio>()
    private var root: RecipeNode? = null
    private var subProcessList = arrayListOf<Process>()
    private var degreeMap = hashMapOf<Recipe, Int>()

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
        val newList = list.filterNot {
            it.first is ProcessRecipe || it.first is OreChainRecipe
        }.reversed()
        recipeList.clear()
        recipeList.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitProcess(process: Process) {
        root = process.getRecipeDFSTree()
        preOrderTraverse(0, root!!)
        notifyDataSetChanged()
    }

    fun submitSubProcessList(subProcess: List<Process>) {
        subProcessList.clear()
        subProcessList.addAll(subProcess)
        root?.let { preOrderTraverse(0, it) }
    }

    private fun preOrderTraverse(degree: Int, node: RecipeNode) {
        val recipe = node.recipe
        degreeMap[recipe] = degree
        if (recipe is ProcessRecipe) {
            val processId = recipe.getProcessId()
            val subProcess = subProcessList.find { it.id == processId }
            if (subProcess != null) {
                preOrderTraverse(degree + 1, subProcess.getRecipeDFSTree())
            }
        }
        for (child in node.childNodes) {
            preOrderTraverse(degree + 1, child)
        }
    }

    inner class ProcessingOrderViewHolder(itemView: View)
        : BindableViewHolder<RecipeRatio>(itemView) {
        private val label: ImageView = itemView.findViewById(R.id.degree_label)
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val caption: TextView = itemView.findViewById(R.id.caption)
        private val ratio: TextView = itemView.findViewById(R.id.ratio)

        private val format = DecimalFormat("#.##")

        private val context: Context
            get() = itemView.context

        override fun bindNotNull(model: RecipeRatio) {
            val (recipe, ratio) = model
            val degree = degreeMap[recipe] ?: 0
            machineName.text = when (recipe) {
                is MachineRecipeView -> recipe.machineName
                is CraftingRecipeView -> context.getString(R.string.txt_crafting_table)
                is SupplierRecipe -> context.getString(R.string.txt_supplier)
                is OreChainRecipe -> context.getString(R.string.txt_ore_chain_recipe)
                is ProcessRecipe -> context.getString(R.string.txt_process_reference)
                else -> context.getString(R.string.txt_unnamed)
            }
            caption.text = context.formatString(
                R.string.form_processing_order,
                degree,
                recipe.getOutput().joinToString(", ") { it.localizedName }
            )
            this.ratio.text = context.formatString(
                R.string.form_ratio,
                format.format(ratio)
            )

            val colorList = getDegreeColorList(context)
            val index = min(colorList.size - 1, degree)
            label.setImageDrawable(ColorDrawable(colorList[index]))
        }
    }

    companion object {
        private var degreeColors: IntArray? = null

        private fun getDegreeColorList(context: Context): IntArray {
            if (degreeColors == null) {
                degreeColors = context.resources.getIntArray(R.array.degreeColorList)
            }
            return degreeColors!!
        }
    }
}