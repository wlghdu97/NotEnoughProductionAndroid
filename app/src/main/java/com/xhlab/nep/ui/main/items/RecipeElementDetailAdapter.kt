package com.xhlab.nep.ui.main.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.shared.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.setIcon

class RecipeElementDetailAdapter(
    private val listener: ElementListener? = null
) : PagingDataAdapter<RecipeElement, RecipeElementDetailAdapter.RecipeElementViewHolder>(
    DiffCallback
) {
    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeElementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_element, parent, false)
        return RecipeElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeElementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class RecipeElementViewHolder(itemView: View) :
        BindableViewHolder<RecipeElement>(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val unlocalizedName: TextView = itemView.findViewById(R.id.unlocalized_name)
        private val type: TextView = itemView.findViewById(R.id.type)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.id, it.type) }
            }
        }

        override fun bindNotNull(model: RecipeElement) {
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
            name.text = when (model.localizedName.isEmpty()) {
                true -> itemView.context.getString(R.string.txt_unnamed)
                false -> model.localizedName.trim()
            }
            unlocalizedName.text = when (model.unlocalizedName.isEmpty()) {
                true -> itemView.context.getString(R.string.txt_unnamed)
                false -> model.unlocalizedName.trim()
            }
            type.setText(
                when (model.type) {
                    Element.ITEM -> R.string.txt_item
                    Element.FLUID -> R.string.txt_fluid
                    else -> R.string.txt_unknown
                }
            )
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<RecipeElement>() {
        override fun areItemsTheSame(oldItem: RecipeElement, newItem: RecipeElement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecipeElement, newItem: RecipeElement): Boolean {
            return (oldItem.id == newItem.id &&
                    oldItem.unlocalizedName == newItem.unlocalizedName &&
                    oldItem.localizedName == newItem.localizedName &&
                    oldItem.type == newItem.type &&
                    oldItem.metaData == newItem.metaData)
        }
    }
}
