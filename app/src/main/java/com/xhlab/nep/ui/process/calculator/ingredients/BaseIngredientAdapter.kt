package com.xhlab.nep.ui.process.calculator.ingredients

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.ui.adapters.ElementViewHolder
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.layoutInflater
import java.text.DecimalFormat

class BaseIngredientAdapter(
    private val listener: ElementKeyListener? = null
) : RecyclerView.Adapter<BaseIngredientAdapter.BaseIngredientViewHolder>() {

    private val elementList = arrayListOf<ElementView>()
    private val ratioList = arrayListOf<Double>()
    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseIngredientViewHolder {
        val view = parent.context.layoutInflater
            .inflate(R.layout.holder_element, parent, false)
        return BaseIngredientViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseIngredientViewHolder, position: Int) {
        holder.bind(elementList[position])
    }

    override fun getItemCount() = elementList.size

    fun submitList(list: List<Pair<ElementView, Double>>) {
        val sortedList = list.sortedBy { it.first.unlocalizedName }
        elementList.clear()
        elementList.addAll(sortedList.map { it.first })
        ratioList.clear()
        ratioList.addAll(sortedList.map { it.second })
        notifyDataSetChanged()
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class BaseIngredientViewHolder(itemView: View) : ElementViewHolder(itemView) {
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
            name.text = context.formatString(
                R.string.form_item_with_amount,
                format.format(ratioList[adapterPosition]),
                getNameText(model)
            )
        }
    }
}