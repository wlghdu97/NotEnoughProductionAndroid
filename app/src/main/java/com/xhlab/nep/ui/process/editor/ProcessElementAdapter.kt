package com.xhlab.nep.ui.process.editor

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.process.SupplierRecipe
import com.xhlab.nep.ui.main.items.ElementListener
import com.xhlab.nep.ui.main.viewholders.ElementViewHolder
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.layoutInflater
import kotlin.math.min

class ProcessElementAdapter(
    private val listener: ElementListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var process: Process? = null
    private var recipeNode: RecipeNode? = null
    private var outputListSize = 0

    private val elementList = arrayListOf<Element>()

    private var isIconVisible = false
    private var showConnection = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            0 -> R.layout.holder_element
            1 -> R.layout.holder_recipe_node_element_header
            else -> throw IllegalArgumentException()
        }
        val view = parent.context.layoutInflater.inflate(layout, parent, false)
        return if (viewType == 0) {
            ProcessElementViewHolder(view)
        } else {
            HeaderViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderPosition(position)) {
            (holder as HeaderViewHolder).bind(when (position == 0) {
                true -> "Output"
                false -> "Input"
            })
        } else {
            val elementPosition = position - 1 - if (position > outputListSize) 1 else 0
            (holder as ProcessElementViewHolder).bind(elementList[elementPosition])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (isHeaderPosition(position)) {
            true -> 1
            false -> 0
        }
    }

    override fun getItemCount(): Int {
        return recipeNode?.let {
            val inputListSize = it.recipe.getInputs().size
            outputListSize = it.recipe.getOutput().size
            // if input list is empty, hide input header
            outputListSize + inputListSize + min(inputListSize, 1) + 1
        } ?: 0
    }

    fun submitRecipeNode(process: Process?, node: RecipeNode) {
        this.process = process
        this.recipeNode = node
        val newList = node.recipe.getOutput() + node.recipe.getInputs()
        val callback = getDiffer(elementList, newList)
        val result = DiffUtil.calculateDiff(callback)

        elementList.clear()
        elementList.addAll(newList)
        result.dispatchUpdatesTo(this)
    }

    fun setIconVisible(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    fun setShowConnection(mode: Boolean) {
        showConnection = mode
        notifyDataSetChanged()
    }

    private fun isHeaderPosition(position: Int) = (position == 0 || position == outputListSize + 1)

    inner class ProcessElementViewHolder(itemView: View) : ElementViewHolder(itemView) {

        private val context: Context
            get() = itemView.context

        override fun bindNotNull(model: Element) {
            super.bindNotNull(model)

            if (!showConnection && isIconVisible) {
                icon.setIcon(model.unlocalizedName)
                icon.imageTintList = null
                icon.rotation = 0f
                icon.scaleX = 1f
                icon.scaleY = 1f
            } else {
                val process = process
                val recipeNode = recipeNode
                val status = if (process != null && recipeNode != null) {
                    process.getConnectionStatus(recipeNode.recipe, model)
                } else Process.ConnectionStatus.UNCONNECTED
                when (status) {
                    Process.ConnectionStatus.CONNECTED_TO_CHILD -> {
                        icon.imageResource = R.drawable.ic_power_24dp
                        icon.imageTintList = getColorStateList(R.color.colorPluggedToChild)
                        icon.rotation = 180f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    Process.ConnectionStatus.CONNECTED_TO_PARENT -> {
                        icon.imageResource = R.drawable.ic_power_24dp
                        icon.imageTintList = getColorStateList(R.color.colorPluggedToParent)
                        icon.rotation = 0f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    Process.ConnectionStatus.UNCONNECTED -> {
                        icon.imageResource = R.drawable.ic_outlet_24dp
                        icon.imageTintList = getColorStateList(R.color.colorUnplugged)
                        icon.rotation = 0f
                        icon.scaleX = 0.8f
                        icon.scaleY = 0.8f
                    }
                    Process.ConnectionStatus.FINAL_OUTPUT -> {
                        icon.imageResource = R.drawable.ic_flag_24dp
                        icon.imageTintList = getColorStateList(R.color.colorFinalOutput)
                        icon.rotation = 0f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    Process.ConnectionStatus.NOT_CONSUMED -> {
                        icon.imageResource = R.drawable.ic_power_off_24dp
                        icon.imageTintList = getColorStateList(R.color.colorInfinite)
                        icon.rotation = 180f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                }
            }

            name.text = if (recipeNode?.recipe is SupplierRecipe) {
                model.localizedName
            } else {
                context.formatString(
                    R.string.form_item_with_amount,
                    model.amount,
                    model.localizedName
                )
            }
        }

        private fun getColorStateList(@ColorRes color: Int): ColorStateList {
            return ColorStateList.valueOf(ContextCompat.getColor(itemView.context, color))
        }
    }

    inner class HeaderViewHolder(itemView: View) : BindableViewHolder<String>(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)

        override fun bindNotNull(model: String) {
            title.text = model
        }
    }

    private fun getDiffer(
        oldList: List<Element>,
        newList: List<Element>
    ): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].unlocalizedName == newList[newItemPosition].unlocalizedName
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }
        }
    }
}