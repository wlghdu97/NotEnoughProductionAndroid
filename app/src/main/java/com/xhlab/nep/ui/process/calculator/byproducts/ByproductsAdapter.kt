package com.xhlab.nep.ui.process.calculator.byproducts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.ui.adapters.RecipeElementViewHolder
import com.xhlab.nep.ui.process.calculator.cycles.RecipeRatio
import com.xhlab.nep.ui.process.calculator.ingredients.ElementKeyListener
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import java.text.DecimalFormat

class ByproductsAdapter(
    private val listener: ElementKeyListener? = null
) : RecyclerView.Adapter<ByproductsAdapter.ByproductViewHolder>() {

    private val elementRatioMap = hashMapOf<RecipeElement, Double>()
    private val elementList = arrayListOf<RecipeElement>()
    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ByproductViewHolder {
        val view = LayoutInflater.from(parent.context)
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
        val elements = mutableMapOf<RecipeElement, Double>()
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
        val list = arrayListOf<RecipeElement>()
        preOrderTraverse(process, 0, process.getRecipeDFSTree(), list)
        elementList.clear()
        elementList.addAll(list.distinct())
        notifyDataSetChanged()
    }

    private fun preOrderTraverse(
        process: Process,
        degree: Int,
        node: RecipeNode,
        list: ArrayList<RecipeElement>
    ) {
        val recipe = node.recipe
        for (element in recipe.getOutput()) {
            val connections = process.getConnectionStatus(recipe, element)
            if (connections.size == 1 &&
                connections[0].status == Process.ConnectionStatus.UNCONNECTED
            ) {
                list.add(element)
            }
        }
        for (child in node.childNodes) {
            preOrderTraverse(process, degree + 1, child, list)
        }
    }

    inner class ByproductViewHolder(itemView: View) : RecipeElementViewHolder(itemView) {
        private val format = DecimalFormat("#.##")

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.unlocalizedName) }
            }
        }

        override fun bindNotNull(model: RecipeElement) {
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
