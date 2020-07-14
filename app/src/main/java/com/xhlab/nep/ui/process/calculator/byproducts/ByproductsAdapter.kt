package com.xhlab.nep.ui.process.calculator.byproducts

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.ui.adapters.ElementViewHolder
import com.xhlab.nep.ui.process.calculator.cycles.RecipeRatio
import com.xhlab.nep.ui.process.calculator.ingredients.ElementKeyListener
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.layoutInflater
import java.text.DecimalFormat

class ByproductsAdapter(
    private val listener: ElementKeyListener? = null
) : RecyclerView.Adapter<ByproductsAdapter.ByproductViewHolder>() {

    private var root: Process? = null
    private var subProcessList = arrayListOf<Process>()

    private val elementRatioMap = hashMapOf<Element, Double>()
    private val elementList = arrayListOf<Element>()
    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ByproductViewHolder {
        val view = parent.context.layoutInflater
            .inflate(R.layout.holder_element, parent, false)
        return ByproductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ByproductViewHolder, position: Int) {
        holder.bind(elementList[position])
    }

    override fun getItemCount() = elementList.size

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    fun submitRecipeRatioList(list: List<RecipeRatio>) {
        val elements = mutableMapOf<Element, Double>()
        for ((recipe, ratio) in list) {
            for (element in recipe.getOutput()) {
                elements[element] = ratio
            }
        }
        elementRatioMap.clear()
        elementRatioMap.putAll(elements)
        notifyDataSetChanged()
    }

    fun submitProcess(process: Process) {
        root = process
        calculateByproducts()
    }

    fun submitSubProcessList(subProcess: List<Process>) {
        subProcessList.clear()
        subProcessList.addAll(subProcess)
        calculateByproducts()
    }

    private fun calculateByproducts() {
        elementList.clear()
        root?.let { root ->
            val list = arrayListOf<Element>()
            preOrderTraverse(root, 0, root.getRecipeDFSTree(), list)
            elementList.addAll(list.distinct())
            for (subProcess in subProcessList) {
                val sublist = arrayListOf<Element>()
                preOrderTraverse(subProcess, 0, subProcess.getRecipeDFSTree(), sublist)
                elementList.addAll(sublist.distinct())
            }
        }
        notifyDataSetChanged()
    }

    private fun preOrderTraverse(
        process: Process,
        degree: Int,
        node: RecipeNode,
        list: ArrayList<Element>
    ) {
        val recipe = node.recipe
        for (element in recipe.getOutput()) {
            val connections = process.getConnectionStatus(recipe, element)
            if (connections.size == 1 &&
                connections[0].status == Process.ConnectionStatus.UNCONNECTED) {
                list.add(element)
            }
        }
        for (child in node.childNodes) {
            preOrderTraverse(process, degree + 1, child, list)
        }
    }

    inner class ByproductViewHolder(itemView: View) : ElementViewHolder(itemView) {
        private val format = DecimalFormat("#.##")

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.unlocalizedName) }
            }
        }

        override fun bindNotNull(model: Element) {
            super.bindNotNull(model)
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
            val ratio = (elementRatioMap[model] ?: 0.0) * model.amount
            name.text = context.formatString(
                R.string.form_item_with_amount,
                format.format(ratio),
                getNameText(model)
            )
        }
    }
}