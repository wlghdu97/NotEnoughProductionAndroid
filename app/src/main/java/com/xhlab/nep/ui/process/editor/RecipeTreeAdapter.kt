package com.xhlab.nep.ui.process.editor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isGone
import com.xhlab.nep.R
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.ui.process.adapters.ProcessTreeAdapter
import com.xhlab.nep.ui.process.adapters.ProcessTreeViewHolder
import com.xhlab.nep.ui.process.adapters.RecipeViewDegreeNode
import com.xhlab.nep.ui.process.adapters.toDegreeNode

class RecipeTreeAdapter(
    private val treeListener: ProcessTreeListener? = null,
    private val processEditListener: ProcessEditListener? = null
) : ProcessTreeAdapter() {

    private val expandedNodes = mutableSetOf<Recipe>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.holder_recipe_node, parent, false)
        return TreeViewHolder(view)
    }

    override fun getOrderedList(root: RecipeNode): ArrayList<RecipeViewDegreeNode> {
        val list = arrayListOf<RecipeViewDegreeNode>()
        preOrderTraverse(0, null, root, list)
        return list
    }

    private fun preOrderTraverse(
        degree: Int,
        prevNode: RecipeNode?,
        node: RecipeNode,
        list: ArrayList<RecipeViewDegreeNode>
    ) {
        if (prevNode == null || expandedNodes.contains(prevNode.recipe)) {
            list.add(node.toDegreeNode(process!!, degree))
        } else {
            expandedNodes.remove(node.recipe)
        }
        for (child in node.childNodes) {
            preOrderTraverse(degree + 1, node, child, list)
        }
    }

    inner class TreeViewHolder(itemView: View) : ProcessTreeViewHolder(itemView) {
        private val header: ViewGroup = itemView.findViewById(R.id.header)
        private val chevron: ImageView = itemView.findViewById(R.id.chevron)
        private val elementListAdapter = ElementAdapter(processEditListener)

        init {
            header.setOnClickListener {
                model?.let {
                    if (expandedNodes.contains(it.node.recipe)) {
                        expandedNodes.remove(it.node.recipe)
                        chevron.animate().rotation(90f)
                    } else {
                        expandedNodes.add(it.node.recipe)
                        chevron.animate().rotation(270f)

                        val position = adapterPosition + it.node.childNodes.size
                        treeListener?.onProcessTreeExpanded(position)
                    }
                    recalculateVisibleList()
                }
            }
            elementList.adapter = elementListAdapter
        }

        override fun bindNotNull(model: RecipeViewDegreeNode) {
            super.bindNotNull(model)
            chevron.isGone = model.node.childNodes.isEmpty()
            if (!expandedNodes.contains(model.node.recipe)) {
                chevron.rotation = 90f
            } else {
                chevron.rotation = 270f
            }

            with (elementListAdapter) {
                submitConnectionList(model, model.connectionList)
                setIconVisible(isIconVisible)
                setShowConnection(showConnection)
            }
        }
    }

    interface ProcessTreeListener {
        fun onProcessTreeExpanded(position: Int)
    }
}