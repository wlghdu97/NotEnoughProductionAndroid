package com.xhlab.nep.ui.main.items

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.FLUID
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.setIcon

class ElementDetailAdapter (
    private val listener: ElementListener? = null
) : PagedListAdapter<ElementView, ElementDetailAdapter.RecipeElementViewHolder>(DiffCallback) {

    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeElementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_element, parent, false)
        return RecipeElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeElementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun submitList(pagedList: PagedList<ElementView>?) {
        super.submitList(pagedList)
        notifyDataSetChanged()
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class RecipeElementViewHolder(itemView: View)
        : BindableViewHolder<ElementView>(itemView) {

        private val icon: ImageView = itemView.findViewById(R.id.icon)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val unlocalizedName: TextView = itemView.findViewById(R.id.unlocalized_name)
        private val type: TextView = itemView.findViewById(R.id.type)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.id, it.type) }
            }
        }

        override fun bindNotNull(model: ElementView) {
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
            type.setText(when (model.type) {
                ITEM -> R.string.txt_item
                FLUID -> R.string.txt_fluid
                else -> R.string.txt_unknown
            })
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ElementView>() {
        override fun areItemsTheSame(oldItem: ElementView, newItem: ElementView): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ElementView, newItem: ElementView): Boolean {
            return (oldItem.id == newItem.id &&
                    oldItem.unlocalizedName == newItem.unlocalizedName &&
                    oldItem.localizedName == newItem.localizedName &&
                    oldItem.type == newItem.type &&
                    oldItem.metaData == newItem.metaData)
        }
    }
}