package com.xhlab.nep.ui.element.replacements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.ui.util.BindableViewHolder

class OreDictAdapter(
    private val listener: OreDictListener? = null
) : PagingDataAdapter<String, OreDictAdapter.OreDictViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OreDictViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_ore_dict, parent, false)
        return OreDictViewHolder(view)
    }

    override fun onBindViewHolder(holder: OreDictViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OreDictViewHolder(itemView: View) : BindableViewHolder<String>(itemView) {
        private val oreDictName: TextView = itemView.findViewById(R.id.ore_dict_name)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClicked(it) }
            }
        }

        override fun bindNotNull(model: String) {
            oreDictName.text = model
        }
    }
}
