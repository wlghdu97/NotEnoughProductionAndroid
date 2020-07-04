package com.xhlab.nep.ui.process.editor.selection

import android.view.View
import android.view.ViewGroup
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.ui.process.adapters.ProcessTreeAdapter
import com.xhlab.nep.ui.process.adapters.ProcessTreeViewHolder
import com.xhlab.nep.ui.process.adapters.RecipeViewDegreeNode
import com.xhlab.nep.ui.process.adapters.toDegreeNode
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import org.jetbrains.anko.layoutInflater
import java.util.*

class RecipeSelectionAdapter(
    private val listener: RecipeSelectionListener? = null
) : ProcessTreeAdapter() {

    private var constraint: ProcessEditViewModel.ConnectionConstraint? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectionViewHolder {
        val view = parent.context.layoutInflater
            .inflate(R.layout.holder_recipe_selection_node, parent, false)
        return SelectionViewHolder(view)
    }

    fun setConnectionConstraint(constraint: ProcessEditViewModel.ConnectionConstraint) {
        this.constraint = constraint
        recalculateVisibleList()
    }

    // level-order traversal
    override fun getOrderedList(root: RecipeNode): ArrayList<RecipeViewDegreeNode> {
        return constraint?.let { constraint ->
            val list = arrayListOf<RecipeViewDegreeNode>()
            val queue: Queue<Pair<Int, RecipeNode>> = LinkedList<Pair<Int, RecipeNode>>()
            queue.add(0 to root)

            val reversed = isReversed(constraint)
            do {
                val degreeNode = queue.poll()
                if (degreeNode != null) {
                    val (degree, node) = degreeNode
                    val isNodeInDegree = when (constraint.connectToParent) {
                        true -> degree < constraint.degree
                        false -> degree > constraint.degree
                    }
                    val isNodeVisible = if (isNodeInDegree) {
                        getKeyElement(node, constraint, reversed) != null
                    } else {
                        false
                    }
                    if (isNodeVisible) {
                        list.add(node.toDegreeNode(process!!, degree))
                    }
                    queue.addAll(node.childNodes.map { degree + 1 to it })
                }
            } while (degreeNode != null)

            return list
        } ?: arrayListOf()
    }

    private fun isReversed(constraint: ProcessEditViewModel.ConnectionConstraint): Boolean {
        val keyInOutput = constraint.recipe.getOutput().find {
            it.unlocalizedName == constraint.elementKey
        } != null
        return constraint.connectToParent xor keyInOutput
    }

    private fun getKeyElement(
        node: RecipeNode,
        constraint: ProcessEditViewModel.ConnectionConstraint,
        isReversed: Boolean? = isReversed(constraint)
    ): Element? {
        return if (constraint.connectToParent xor (isReversed == true)) {
            node.recipe.getInputs().find {
                it.unlocalizedName == constraint.elementKey
            }
        } else {
            node.recipe.getOutput().find {
                it.unlocalizedName == constraint.elementKey
            }
        }
    }

    inner class SelectionViewHolder(itemView: View) : ProcessTreeViewHolder(itemView) {
        private val elementListAdapter = PlainElementAdapter()

        init {
            itemView.setOnClickListener {
                val constraint = constraint
                val node = model?.node
                if (constraint != null && node != null) {
                    val reversed = isReversed(constraint)
                    val from = constraint.recipe
                    val to = node.recipe
                    val element = getKeyElement(node, constraint, reversed)
                    if (element != null) {
                        when (constraint.connectToParent xor reversed) {
                            true -> listener?.onSelect(from, to, element, reversed)
                            false -> listener?.onSelect(to, from, element, reversed)
                        }
                    }
                }
            }
            elementList.adapter = elementListAdapter
        }

        override fun bindNotNull(model: RecipeViewDegreeNode) {
            super.bindNotNull(model)
            with (elementListAdapter) {
                submitConnectionList(model, model.connectionList)
                setIconVisible(isIconVisible)
                setShowConnection(showConnection)
            }
        }
    }
}