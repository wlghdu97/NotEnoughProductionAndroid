package com.xhlab.nep.ui.process.editor

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xhlab.nep.R
import com.xhlab.nep.model.process.Process
import com.xhlab.nep.model.process.RecipeNode
import com.xhlab.nep.model.process.SupplierRecipe
import com.xhlab.nep.model.recipes.MachineRecipe
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.ui.util.BindableViewHolder
import com.xhlab.nep.util.formatString
import org.jetbrains.anko.layoutInflater
import java.text.NumberFormat
import java.util.*
import kotlin.math.min

class ProcessTreeAdapter(
    private val treeListener: ProcessTreeListener? = null
) : RecyclerView.Adapter<ProcessTreeAdapter.TreeViewHolder>() {

    private var process: Process? = null
    private var root: RecipeNode? = null

    private val visibleList = arrayListOf<RecipeViewDegreeNode>()
    private val expandedNodes = mutableSetOf<RecipeNode>()

    private var isIconVisible = false
    private var showConnection = true

    private val integerFormat = NumberFormat.getIntegerInstance(Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = parent.context.layoutInflater.inflate(R.layout.holder_recipe_node, parent, false)
        return TreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
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

    private fun recalculateVisibleList() {
        root?.let {
            val newVisibleList = getPreOrderedList(it)
            val callback = getDiffer(visibleList, newVisibleList)
            val result = DiffUtil.calculateDiff(callback)

            visibleList.clear()
            visibleList.addAll(newVisibleList)
            result.dispatchUpdatesTo(this)
        }
    }

    private fun getPreOrderedList(root: RecipeNode): ArrayList<RecipeViewDegreeNode> {
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
        if (prevNode == null || expandedNodes.contains(prevNode)) {
            list.add(RecipeViewDegreeNode(degree, node))
        } else {
            expandedNodes.remove(node)
        }
        for (child in node.childNodes) {
            preOrderTraverse(degree + 1, node, child, list)
        }
    }

    inner class TreeViewHolder(itemView: View) : BindableViewHolder<RecipeViewDegreeNode>(itemView) {
        private val label: ImageView = itemView.findViewById(R.id.degree_label)
        private val header: ViewGroup = itemView.findViewById(R.id.header)
        private val machineName: TextView = itemView.findViewById(R.id.machine_name)
        private val degree: TextView = itemView.findViewById(R.id.degree)
        private val chevron: ImageView = itemView.findViewById(R.id.chevron)
        private val properties: TextView = itemView.findViewById(R.id.recipe_properties)
        private val elementList: RecyclerView = itemView.findViewById(R.id.element_list)

        private val elementListAdapter = ProcessElementAdapter()

        private val context: Context
            get() = itemView.context

        init {
            header.setOnClickListener {
                model?.let {
                    if (expandedNodes.contains(it.node)) {
                        expandedNodes.remove(it.node)
                        chevron.animate().rotation(90f)
                    } else {
                        expandedNodes.add(it.node)
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
            val recipe = model.node.recipe
            machineName.text = when (recipe) {
                is MachineRecipeView -> recipe.machineName
                is SupplierRecipe -> context.getString(R.string.txt_supplier)
                else -> context.getString(R.string.txt_unnamed)
            }

            degree.text = context.formatString(R.string.form_degree, model.degree)

            chevron.isGone = model.node.childNodes.isEmpty()
            if (!expandedNodes.contains(model.node)) {
                chevron.rotation = 90f
            } else {
                chevron.rotation = 270f
            }

            if (recipe is MachineRecipeView) {
                val unit = when (recipe.powerType) {
                    MachineRecipe.Companion.PowerType.EU.type -> context.getString(R.string.txt_eu)
                    MachineRecipe.Companion.PowerType.RF.type -> context.getString(R.string.txt_rf)
                    else -> context.getString(R.string.txt_unknown)
                }
                val unitTick = "$unit${context.getString(R.string.txt_per_tick)}"
                val durationSec = recipe.duration / 20f
                val total = recipe.ept.toLong() * recipe.duration
                properties.isGone = false
                properties.text = context.formatString(
                    R.string.form_machine_property,
                    integerFormat.format(recipe.ept),
                    unitTick,
                    integerFormat.format(durationSec),
                    integerFormat.format(total),
                    unit
                )
            } else {
                properties.isGone = true
            }

            with (elementListAdapter) {
                submitRecipeNode(process, model.node)
                setIconVisible(isIconVisible)
                setShowConnection(showConnection)
            }

            val colorList = getDegreeColorList(context)
            val index = min(colorList.size - 1, model.degree)
            label.setImageDrawable(ColorDrawable(colorList[index]))
        }
    }

    private var degreeColors: IntArray? = null

    private fun getDegreeColorList(context: Context): IntArray {
        if (degreeColors == null) {
            degreeColors = context.resources.getIntArray(R.array.degreeColorList)
        }
        return degreeColors!!
    }

    private fun getDiffer(
        oldList: List<RecipeViewDegreeNode>,
        newList: List<RecipeViewDegreeNode>
    ): DiffUtil.Callback {
        return object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].node.recipe == newList[newItemPosition].node.recipe
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

    data class RecipeViewDegreeNode(val degree: Int, val node: RecipeNode)

    interface ProcessTreeListener {
        fun onProcessTreeExpanded(position: Int)
    }
}