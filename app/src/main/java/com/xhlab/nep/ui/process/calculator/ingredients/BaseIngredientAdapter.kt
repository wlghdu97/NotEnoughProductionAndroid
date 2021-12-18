package com.xhlab.nep.ui.process.calculator.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.process.calculator.ingredients.ElementKeyListener
import com.xhlab.nep.ui.adapters.RecipeElementViewHolder
import com.xhlab.nep.ui.process.adapters.getRecipeElementDiffer
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import java.text.DecimalFormat

class BaseIngredientAdapter(
    private val listener: ElementKeyListener? = null
) : RecyclerView.Adapter<BaseIngredientAdapter.BaseIngredientViewHolder>() {

    private val elementList = arrayListOf<RecipeElement>()
    private val ratioList = arrayListOf<Double>()
    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseIngredientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_element, parent, false)
        return BaseIngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseIngredientViewHolder, position: Int) {
        holder.bind(elementList[position])
    }

    override fun getItemCount() = elementList.size

    fun submitList(list: List<Pair<RecipeElement, Double>>) {
        val sortedList = list.sortedBy { it.first.unlocalizedName }
        val sortedElementList = sortedList.map { it.first }
        val result = DiffUtil.calculateDiff(getRecipeElementDiffer(elementList, sortedElementList))
        with(elementList) {
            clear()
            addAll(sortedElementList)
        }
        with(ratioList) {
            clear()
            addAll(sortedList.map { it.second })
        }
        result.dispatchUpdatesTo(this)
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyItemRangeChanged(0, itemCount)
    }

    inner class BaseIngredientViewHolder(itemView: View) : RecipeElementViewHolder(itemView) {
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
            name.text = context.formatString(
                R.string.form_item_with_amount,
                format.format(ratioList[adapterPosition]),
                getNameText(model)
            )
        }
    }
}
