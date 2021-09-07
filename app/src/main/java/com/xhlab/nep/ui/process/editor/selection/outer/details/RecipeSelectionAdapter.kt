package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.adapters.RecipeDetailViewHolder
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import com.xhlab.nep.ui.process.editor.selection.outer.OreDictRecipeSelectionListener
import com.xhlab.nep.ui.process.editor.selection.select

class RecipeSelectionAdapter(
    targetElementId: Long?,
    private val selectionListener: RecipeSelectionListener? = null,
    private val oreDictSelectionListener: OreDictRecipeSelectionListener? = null
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
                val targetElementId = targetElementId
                if (constraint != null && node != null && targetElementId != null) {
                    constraint.select(
                        node, targetElementId, selectionListener, oreDictSelectionListener
                    )
                }
            }
        }
    }
}
