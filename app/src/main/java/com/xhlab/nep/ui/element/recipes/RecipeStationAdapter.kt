package com.xhlab.nep.ui.element.recipes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.shared.domain.recipe.model.StationView
import com.xhlab.nep.ui.main.machines.MachineListener
import com.xhlab.nep.ui.util.BindableViewHolder

class RecipeStationAdapter (
    private val listener: MachineListener? = null
) : PagedListAdapter<StationView, RecipeStationAdapter.RecipeStationViewHolder>(
    object : DiffUtil.ItemCallback<StationView>() {
        override fun areItemsTheSame(oldItem: StationView, newItem: StationView): Boolean {
            return oldItem.stationName == newItem.stationName
        }

        override fun areContentsTheSame(oldItem: StationView, newItem: StationView): Boolean {
            return oldItem == newItem
        }
    }
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeStationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_station, parent, false)
        return RecipeStationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeStationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeStationViewHolder(itemView: View)
        : BindableViewHolder<StationView>(itemView) {

        private val stationName: TextView = itemView.findViewById(R.id.station_name)
        private val recipeCount: TextView = itemView.findViewById(R.id.recipe_count)

        init {
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.stationId) }
            }
        }

        override fun bindNotNull(model: StationView) {
            stationName.text = model.stationName
            recipeCount.text = model.recipeCount.toString()
        }
    }
}