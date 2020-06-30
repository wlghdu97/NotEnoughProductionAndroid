package com.xhlab.nep.ui.recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.recipes.view.RecipeElementView
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.main.viewholders.ElementViewHolder
import com.xhlab.nep.ui.util.DiffCallback
import com.xhlab.nep.util.setIcon

class RecipeElementAdapter(
    private val listener: ElementListener? = null
) : RecyclerView.Adapter<ElementViewHolder>() {

    private val elementList = ArrayList<RecipeElementView>()
    private var isIconVisible = false

    fun submitList(list: List<RecipeElementView>) {
        val callback = RecipeElementDiffCallback(
            elementList,
            list
        )
        val result = DiffUtil.calculateDiff(callback)

        elementList.clear()
        elementList.addAll(list)
        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(when (viewType) {
                0 -> R.layout.holder_element
                1 -> R.layout.holder_element_ore_chain
                else -> throw IllegalArgumentException("invalid view type.")
            }, parent, false)
        return DefaultElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: ElementViewHolder, position: Int) {
        holder.bind(elementList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (elementList[position].type) {
            ORE_CHAIN -> 1
            else -> 0
        }
    }

    override fun getItemCount() = elementList.size

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class DefaultElementViewHolder(itemView: View) : ElementViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val model = model
                if (model != null && model is ElementView) {
                    listener?.onClick(model.id, model.type)
                }
            }
        }

        override fun bindNotNull(model: Element) {
            super.bindNotNull(model)
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
        }
    }

    private class RecipeElementDiffCallback(
        private val oldList: List<RecipeElementView>,
        private val newList: List<RecipeElementView>
    ) : DiffCallback<RecipeElementView>(oldList, newList) {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
    }
}