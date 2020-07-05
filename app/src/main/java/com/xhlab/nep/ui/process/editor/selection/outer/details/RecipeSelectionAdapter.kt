package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.adapters.RecipeDetailViewHolder
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import com.xhlab.nep.ui.process.editor.selection.getKeyElement
import com.xhlab.nep.ui.process.editor.selection.isReversed

class RecipeSelectionAdapter(
    targetElementId: Long?,
    private val selectionListener: RecipeSelectionListener? = null
) : RecipeDetailAdapter(targetElementId) {

    private var constraint: ProcessEditViewModel.ConnectionConstraint? = null

    override fun getRecipeLayoutId(): Int {
        return R.layout.holder_recipe_plain
    }

    override fun getMachineRecipeLayoutId(): Int {
        return R.layout.holder_recipe_machine_plain
    }

    override fun createViewHolder(itemView: View): RecipeDetailViewHolder {
        return RecipeSelectionViewHolder(itemView)
    }

    fun setConnectionConstraint(constraint: ProcessEditViewModel.ConnectionConstraint) {
        this.constraint = constraint
    }

    inner class RecipeSelectionViewHolder(itemView: View) : RecipeDetailViewHolder(itemView) {
        override val itemAdapter = RecipeSelectionElementAdapter()
        override val byproductAdapter = RecipeSelectionElementAdapter()

        override val targetElementId: Long?
            get() = this@RecipeSelectionAdapter.targetElementId
        override val isIconVisible: Boolean
            get() = this@RecipeSelectionAdapter.isIconVisible

        init {
            itemView.setOnClickListener {
                val constraint = constraint
                val node = model
                if (constraint != null && node != null) {
                    val reversed = constraint.isReversed()
                    val from = constraint.recipe
                    val to = node
                    val element = constraint.getKeyElement(node, reversed)
                    if (element != null) {
                        when (constraint.connectToParent xor reversed) {
                            true -> selectionListener?.onSelect(from, to, element, reversed)
                            false -> selectionListener?.onSelect(to, from, element, reversed)
                        }
                    }
                }
            }
        }
    }
}