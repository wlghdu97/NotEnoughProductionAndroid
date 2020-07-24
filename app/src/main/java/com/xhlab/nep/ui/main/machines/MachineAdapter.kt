package com.xhlab.nep.ui.main.machines

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.model.Machine
import com.xhlab.nep.ui.util.BindableViewHolder

class MachineAdapter(
    private val listener: MachineListener? = null
) : PagedListAdapter<Machine, MachineAdapter.MachineViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MachineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_machine, parent, false)
        return MachineViewHolder(view)
    }

    override fun onBindViewHolder(holder: MachineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MachineViewHolder(itemView: View) : BindableViewHolder<Machine>(itemView) {
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val modName: TextView = itemView.findViewById(R.id.mod_name)

        init {
            itemView.setOnClickListener { model?.let { listener?.onClick(it.id) } }
        }

        override fun bindNotNull(model: Machine) {
            machineName.text = model.name
            modName.text = model.modName
        }
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<Machine>() {
            override fun areItemsTheSame(oldItem: Machine, newItem: Machine): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Machine, newItem: Machine): Boolean {
                return oldItem == newItem
            }
        }
    }
}