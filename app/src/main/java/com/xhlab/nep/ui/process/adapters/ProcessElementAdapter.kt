package com.xhlab.nep.ui.process.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.ui.util.BindableViewHolder
import org.jetbrains.anko.layoutInflater
import kotlin.math.min

abstract class ProcessElementAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected var recipeNode: RecipeViewDegreeNode? = null
    private var outputListSize = 0

    private val elementList = arrayListOf<ElementConnection>()

    protected var isIconVisible = false
        private set
    protected var showConnection = true
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = when (viewType) {
            0 -> getElementHolderLayoutId()
            1 -> getMultiElementHolderLayoutId()
            2 -> R.layout.holder_recipe_node_element_header
            else -> throw IllegalArgumentException()
        }
        val view = parent.context.layoutInflater.inflate(layout, parent, false)
        return when (viewType) {
            0 -> onCreateElementHolder(view)
            1 -> ProcessElementMultiConnectionViewHolder(view)
            2 -> HeaderViewHolder(view)
            else -> throw IllegalArgumentException()
        }
    }

    protected open fun getElementHolderLayoutId(): Int = R.layout.holder_process_element

    protected open fun getMultiElementHolderLayoutId(): Int = R.layout.holder_process_element_multi

    abstract fun onCreateElementHolder(itemView: View): ProcessElementViewHolder

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
            val inputListSize = it.node.recipe.getInputs().size
            outputListSize = it.node.recipe.getOutput().size
            // if input list is empty, hide input header
            outputListSize + inputListSize + min(inputListSize, 1) + 1
        } ?: 0
    }

    fun submitConnectionList(node: RecipeViewDegreeNode, list: List<ElementConnection>) {
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
                    .inflate(getElementHolderLayoutId(), parent, false)
                return onCreateElementHolder(view)
            }

            override fun onBindViewHolder(holder: ProcessElementViewHolder, position: Int) {
                holder.bind(model)
            }

            override fun getItemCount() = model?.connections?.size ?: 0
        }
    }

    inner class HeaderViewHolder(itemView: View) : BindableViewHolder<String>(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)

        override fun bindNotNull(model: String) {
            title.text = model
        }
    }

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