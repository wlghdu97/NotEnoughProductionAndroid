package com.xhlab.nep.ui.process.editor

import android.content.Context
import android.content.res.ColorStateList
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.Process.ConnectionStatus.*
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.process.SupplierRecipe
import com.xhlab.nep.ui.main.viewholders.ElementViewHolder
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import com.xhlab.nep.util.setIcon
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.layoutInflater
import kotlin.math.min

class ProcessElementAdapter(
    private val processEditListener: ProcessEditListener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recipeNode: RecipeNode? = null
    private var outputListSize = 0

    private val elementList = arrayListOf<ElementConnection>()

    private var isIconVisible = false
    private var showConnection = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            0 -> R.layout.holder_process_element
            1 -> R.layout.holder_process_element_multi
            2 -> R.layout.holder_recipe_node_element_header
            else -> throw IllegalArgumentException()
        }
        val view = parent.context.layoutInflater.inflate(layout, parent, false)
        return when (viewType) {
            0 -> ProcessElementViewHolder(view)
            1 -> ProcessElementMultiConnectionViewHolder(view)
            2 -> HeaderViewHolder(view)
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderPosition(position)) {
            val context = holder.itemView.context
            (holder as HeaderViewHolder).bind(when (position == 0) {
                true -> context.getString(R.string.txt_output)
                false -> context.getString(R.string.txt_input)
            })
        } else {
            val elementPosition = getElementPosition(position)
            when (holder) {
                is ProcessElementViewHolder ->
                    holder.bind(elementList[elementPosition])
                is ProcessElementMultiConnectionViewHolder ->
                    holder.bind(elementList[elementPosition])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (isHeaderPosition(position)) {
            true -> 2
            false -> when (elementList[getElementPosition(position)].connections.size == 1) {
                true -> 0
                false -> 1
            }
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

    fun submitConnectionList(node: RecipeNode, list: List<ElementConnection>) {
        this.recipeNode = node
        val callback = getDiffer(elementList, list)
        val result = DiffUtil.calculateDiff(callback)

        elementList.clear()
        elementList.addAll(list)
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

    private fun isHeaderPosition(position: Int)
            = (position == 0 || position == outputListSize + 1)

    private fun getElementPosition(position: Int)
            = position - 1 - if (position > outputListSize) 1 else 0

    inner class ProcessElementMultiConnectionViewHolder(itemView: View)
        : BindableViewHolder<ElementConnection>(itemView) {
        private val connectionList: RecyclerView = itemView.findViewById(R.id.connection_list)
        private val adapter = MultiConnectionAdapter()

        init {
            connectionList.adapter = adapter
        }

        override fun bindNotNull(model: ElementConnection) {
            adapter.notifyDataSetChanged()
        }

        private inner class MultiConnectionAdapter
            : RecyclerView.Adapter<ProcessElementViewHolder>() {

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ProcessElementViewHolder {
                val view = parent.context.layoutInflater
                    .inflate(R.layout.holder_process_element, parent, false)
                return ProcessElementViewHolder(view)
            }

            override fun onBindViewHolder(holder: ProcessElementViewHolder, position: Int) {
                holder.bind(model)
            }

            override fun getItemCount() = model?.connections?.size ?: 0
        }
    }

    inner class ProcessElementViewHolder(itemView: View)
        : ElementViewHolder(itemView), PopupMenu.OnMenuItemClickListener {
        private val menuButton: ImageButton = itemView.findViewById(R.id.btn_menu)

        private var connectionStatus = Process.Connection(UNCONNECTED, null)
        private val popupMenu = PopupMenu(context, menuButton)
        private val disconnect: MenuItem
        private val connectToParent: MenuItem
        private val connectToChild: MenuItem

        private val context: Context
            get() = itemView.context

        init {
            with (popupMenu) {
                setOnMenuItemClickListener(this@ProcessElementViewHolder)
                inflate(R.menu.process_element_edit)
                disconnect = menu.findItem(R.id.menu_disconnect)
                connectToParent = menu.findItem(R.id.menu_connect_to_parent)
                connectToChild = menu.findItem(R.id.menu_connect_to_child)
            }
            menuButton.setOnClickListener {
                popupMenu.show()
            }
        }

        override fun bindNotNull(model: Element) {
            super.bindNotNull(model)

            connectionStatus = when (model is ElementConnection) {
                true -> model.connections[min(adapterPosition, model.connections.size - 1)]
                false -> Process.Connection(UNCONNECTED, null)
            }

            if (!showConnection && isIconVisible) {
                icon.setIcon(model.unlocalizedName)
                icon.imageTintList = null
                icon.rotation = 0f
                icon.scaleX = 1f
                icon.scaleY = 1f
            } else {
                when (connectionStatus.status) {
                    CONNECTED_TO_CHILD -> {
                        icon.imageResource = R.drawable.ic_power_24dp
                        icon.imageTintList = getColorStateList(R.color.colorPluggedToChild)
                        icon.rotation = 180f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    CONNECTED_TO_PARENT -> {
                        icon.imageResource = R.drawable.ic_power_24dp
                        icon.imageTintList = getColorStateList(R.color.colorPluggedToParent)
                        icon.rotation = 0f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    UNCONNECTED -> {
                        icon.imageResource = R.drawable.ic_outlet_24dp
                        icon.imageTintList = getColorStateList(R.color.colorUnplugged)
                        icon.rotation = 0f
                        icon.scaleX = 0.8f
                        icon.scaleY = 0.8f
                    }
                    FINAL_OUTPUT -> {
                        icon.imageResource = R.drawable.ic_flag_24dp
                        icon.imageTintList = getColorStateList(R.color.colorFinalOutput)
                        icon.rotation = 0f
                        icon.scaleX = 1f
                        icon.scaleY = 1f
                    }
                    NOT_CONSUMED -> {
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

            connectionStatus.status.let {
                menuButton.isGone = it == FINAL_OUTPUT
                disconnect.isVisible = (it == CONNECTED_TO_PARENT || it == CONNECTED_TO_CHILD)
                connectToParent.isVisible = false
                connectToChild.isVisible = false
            }
        }

        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
            val to = recipeNode?.recipe
            val element = model
            return if (to != null && element != null) {
                when (menuItem.itemId) {
                    R.id.menu_disconnect -> {
                        val from = connectionStatus.connectedRecipe
                        val reversed = connectionStatus.reversed
                        if (from != null) {
                            processEditListener?.onDisconnect(from, to, element, reversed)
                        }
                    }
                }
                true
            } else {
                false
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

    data class ElementConnection(
        override val amount: Int,
        override val localizedName: String,
        override val unlocalizedName: String,
        val connections: List<Process.Connection>
    ) : Element()

    private fun getDiffer(
        oldList: List<ElementConnection>,
        newList: List<ElementConnection>
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