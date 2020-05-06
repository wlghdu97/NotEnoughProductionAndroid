package com.xhlab.nep.ui.element.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.shared.domain.recipe.model.RecipeMachineView
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.util.BindableViewHolder

class RecipeMachineAdapter (
    private val listener: MachineListener? = null
) : PagedListAdapter<RecipeMachineView, RecipeMachineAdapter.RecipeMachineViewHolder>(
    object : DiffUtil.ItemCallback<RecipeMachineView>() {
        override fun areItemsTheSame(oldItem: RecipeMachineView, newItem: RecipeMachineView): Boolean {
            return oldItem.machineId == newItem.machineId
        }

        override fun areContentsTheSame(oldItem: RecipeMachineView, newItem: RecipeMachineView): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeMachineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_machine_with_recipe_count, parent, false)
        return RecipeMachineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeMachineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeMachineViewHolder(itemView: View)
        : BindableViewHolder<RecipeMachineView>(itemView) {

        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val modName: TextView = itemView.findViewById(R.id.mod_name)
        private val recipeCount: TextView = itemView.findViewById(R.id.recipe_count)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.machineId) }
            }
        }

        override fun bindNotNull(model: RecipeMachineView) {
            machineName.text = model.machineName
            modName.text = model.modName
            recipeCount.text = model.recipeCount.toString()
        }
    }
}