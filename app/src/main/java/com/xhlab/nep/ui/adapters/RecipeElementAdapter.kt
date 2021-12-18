package com.xhlab.nep.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.DiffCallback
import com.xhlab.nep.util.setIcon

open class RecipeElementAdapter(
    private val listener: ElementListener? = null
) : RecyclerView.Adapter<RecipeElementViewHolder>() {

    private val elementList = ArrayList<RecipeElement>()
    protected var isIconVisible = false

    final override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecipeElementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                when (viewType) {
                    0 -> R.layout.holder_element
                    1 -> R.layout.holder_element_ore_chain
                    else -> throw IllegalArgumentException("invalid view type.")
                }, parent, false
            )
        return createViewHolder(view)
    }

    protected open fun createViewHolder(itemView: View): RecipeElementViewHolder {
        return DefaultElementViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeElementViewHolder, position: Int) {
        holder.bind(elementList[position])
    }

    final override fun getItemViewType(position: Int): Int {
        return when (elementList[position].type) {
            Element.ORE_CHAIN -> 1
            else -> 0
        }
    }

    final override fun getItemCount() = elementList.size

    fun submitList(list: List<RecipeElement>) {
        val callback = RecipeElementDiffCallback(elementList, list)
        val result = DiffUtil.calculateDiff(callback)

        elementList.clear()
        elementList.addAll(list)
        result.dispatchUpdatesTo(this)
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyItemRangeChanged(0, itemCount)
    }

    inner class DefaultElementViewHolder(itemView: View) : RecipeElementViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val model = model
                if (model != null) {
                    listener?.onClick(model.id)
                }
            }
        }

        override fun bindNotNull(model: RecipeElement) {
            super.bindNotNull(model)
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
        }
    }

    private class RecipeElementDiffCallback(
        private val oldList: List<RecipeElement>,
        private val newList: List<RecipeElement>
    ) : DiffCallback<RecipeElement>(oldList, newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
    }
}
