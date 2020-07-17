package com.xhlab.nep.ui.adapters

import android.content.Context
import android.content.res.Resources
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.view.isGone
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.model.process.ProcessSummary
import com.xhlab.nep.ui.main.process.ProcessListener
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.layoutInflater

open class ProcessAdapter(
    private val listener: ProcessListener? = null
) : PagedListAdapter<ProcessSummary, ProcessAdapter.ProcessViewHolder>(Differ) {

    private var isIconVisible = false

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProcessViewHolder {
        val view = parent.context.layoutInflater.inflate(getHolderLayoutId(), parent, false)
        return ProcessViewHolder(view)
    }

    final override fun onBindViewHolder(holder: ProcessViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    protected open fun getHolderLayoutId() = R.layout.holder_process

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class ProcessViewHolder(itemView: View)
        : BindableViewHolder<ProcessSummary>(itemView), PopupMenu.OnMenuItemClickListener
    {
        private val icon: ImageView = itemView.findViewById(R.id.output_icon)
        private val name: TextView = itemView.findViewById(R.id.process_name)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val more: ImageButton? = itemView.findViewById(R.id.btn_more)

        private val context: Context
            get() = itemView.context

        private val resources: Resources
            get() = context.resources

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.processId, it.name) }
            }
            more?.setOnClickListener {
                PopupMenu(itemView.context, more).apply {
                    inflate(R.menu.process_list)
                    setOnMenuItemClickListener(this@ProcessViewHolder)
                }.show()
            }
        }

        override fun bindNotNull(model: ProcessSummary) {
            icon.isGone = !isIconVisible
            if (isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
            name.text = model.name
            val subProcessCount = model.subProcessCount
            val subProcessText = when(subProcessCount == 0) {
                true -> context.getString(R.string.txt_standalone_process)
                false -> resources.getQuantityString(R.plurals.sub_process, subProcessCount, subProcessCount)
            }
            description.text = context.formatString(
                R.string.form_process_description,
                model.amount,
                model.localizedName,
                resources.getQuantityString(R.plurals.node, model.nodeCount, model.nodeCount),
                subProcessText
            )
        }

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            val summary = model
            return if (summary != null) {
                when (menuItem.itemId) {
                    R.id.menu_edit_process_name ->
                        listener?.onRename(summary.processId, summary.name)
                    R.id.menu_export_process_string ->
                        listener?.onExportString(summary.processId)
                    R.id.menu_delete_process ->
                        listener?.onDelete(summary.processId, summary.name)
                }
                true
            } else {
                false
            }
        }
    }

    private object Differ : DiffUtil.ItemCallback<ProcessSummary>() {
        override fun areItemsTheSame(oldItem: ProcessSummary, newItem: ProcessSummary): Boolean {
            return oldItem.processId == newItem.processId
        }

        override fun areContentsTheSame(oldItem: ProcessSummary, newItem: ProcessSummary): Boolean {
            return (oldItem.processId == newItem.processId &&
                    oldItem.name == newItem.name &&
                    oldItem.localizedName == newItem.localizedName &&
                    oldItem.unlocalizedName == newItem.unlocalizedName &&
                    oldItem.amount == newItem.amount &&
                    oldItem.nodeCount == newItem.nodeCount)
        }
    }
}