package com.xhlab.nep.ui.recipe

import android.content.Context
import android.os.Build
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.FLUID
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.shared.domain.recipe.model.RecipeElementView
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.ui.util.DiffCallback
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.textResource
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class RecipeElementAdapter(
    private val listener: ElementListener? = null
) : RecyclerView.Adapter<RecipeElementAdapter.RecipeElementViewHolder>() {

    private val elementList = ArrayList<RecipeElementView>()
    private val integerFormat = NumberFormat.getIntegerInstance(Locale.getDefault())
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeElementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(when (viewType) {
                0 -> R.layout.holder_element
                1 -> R.layout.holder_element_ore_chain
                else -> throw IllegalArgumentException("invalid view type.")
            }, parent, false)
        return RecipeElementViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeElementViewHolder, position: Int) {
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

    inner class RecipeElementViewHolder(itemView: View)
        : BindableViewHolder<RecipeElementView>(itemView) {

        private val icon: ImageView? = itemView.findViewById(R.id.icon)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val unlocalizedName: TextView = itemView.findViewById(R.id.unlocalized_name)
        private val type: TextView? = itemView.findViewById(R.id.type)

        private val context: Context
            get() = itemView.context

        init {
            if (Build.VERSION.SDK_INT >= 23) {
                unlocalizedName.breakStrategy = Layout.BREAK_STRATEGY_BALANCED
            }
            itemView.setOnClickListener {
                model?.let { listener?.onClick(it.id, it.type) }
            }
        }

        override fun bindNotNull(model: RecipeElementView) {
            icon?.isGone = !isIconVisible
            if (icon != null && isIconVisible) {
                icon.setIcon(model.unlocalizedName)
            }
            val metaData = when (!model.metaData.isNullOrEmpty()) {
                true -> " : ${model.metaData}"
                false -> ""
            }
            val localizedName = when (model.localizedName.isEmpty()) {
                true -> context.getString(R.string.txt_unnamed)
                false -> model.localizedName.trim()
            }
            val nameText = "$localizedName$metaData"
            name.text = when (model.amount == 0) {
                true -> nameText
                false -> context.formatString(
                    R.string.form_item_with_amount,
                    integerFormat.format(model.amount),
                    nameText
                )
            }
            unlocalizedName.text = when (model.unlocalizedName.isEmpty()) {
                true -> context.getString(R.string.txt_unnamed)
                false -> model.unlocalizedName
            }

            type?.textResource = when (model.type) {
                ITEM -> R.string.txt_item
                FLUID -> R.string.txt_fluid
                else -> R.string.txt_unknown
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