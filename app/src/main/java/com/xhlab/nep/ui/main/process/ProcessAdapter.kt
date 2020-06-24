package com.xhlab.nep.ui.main.process

import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.model.process.view.ProcessView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.layoutInflater

class ProcessAdapter(
    private val listener: ProcessListener? = null
) : PagedListAdapter<ProcessView, ProcessAdapter.ProcessViewHolder>(Differ) {

    private var isIconVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessViewHolder {
        val view = parent.context.layoutInflater.inflate(R.layout.holder_process, parent, false)
        return ProcessViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProcessViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class ProcessViewHolder(itemView: View) : BindableViewHolder<ProcessView>(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.output_icon)
        private val name: TextView = itemView.findViewById(R.id.process_name)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val more: ImageButton = itemView.findViewById(R.id.btn_more)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.id) }
            }
        }

        override fun bindNotNull(model: ProcessView) {
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.targetOutput.unlocalizedName)
            }
            name.text = model.name
            description.text = String.format(
                itemView.context.getString(R.string.form_process_description),
                model.targetOutput.amount,
                model.targetOutput.localizedName,
                model.getRecipeNodeCount()
            )
        }
    }

    private object Differ : DiffUtil.ItemCallback<ProcessView>() {
        override fun areItemsTheSame(oldItem: ProcessView, newItem: ProcessView): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProcessView, newItem: ProcessView): Boolean {
            return (oldItem.id == newItem.id &&
                    oldItem.name == newItem.name)
        }
    }
}