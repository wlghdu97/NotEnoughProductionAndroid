package com.xhlab.nep.ui.process.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.ui.util.BindableViewHolder

abstract class ProcessTreeAdapter :
    RecyclerView.Adapter<BindableViewHolder<RecipeViewDegreeNode>>() {

    protected var process: Process? = null
    private var root: RecipeNode? = null
    private val visibleList = arrayListOf<RecipeViewDegreeNode>()

    protected var isIconVisible = false
        private set
    protected var showConnection = true
        private set

    override fun onBindViewHolder(holder: BindableViewHolder<RecipeViewDegreeNode>, position: Int) {
        holder.bind(visibleList[position])
    }

    override fun getItemCount() = visibleList.size

    fun submitProcess(process: Process) {
        this.process = process
        this.root = process.getRecipeDFSTree()
        recalculateVisibleList()
    }

    fun setIconVisible(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    fun setShowConnection(mode: Boolean) {
        showConnection = mode
        notifyDataSetChanged()
    }

    protected fun recalculateVisibleList() {
        root?.let {
            val newVisibleList = getOrderedList(it)
            val callback = getDiffer(visibleList, newVisibleList)
            val result = DiffUtil.calculateDiff(callback)

            visibleList.clear()
            visibleList.addAll(newVisibleList)
            result.dispatchUpdatesTo(this)
        }
    }

    abstract fun getOrderedList(root: RecipeNode): ArrayList<RecipeViewDegreeNode>

    private fun getDiffer(
        oldList: List<RecipeViewDegreeNode>,
        newList: List<RecipeViewDegreeNode>
    ): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].node == newList[newItemPosition].node
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
