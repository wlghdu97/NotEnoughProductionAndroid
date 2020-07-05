package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.adapters.RecipeDetailViewHolder

class RecipeSelectionAdapter(targetElementId: Long?) : RecipeDetailAdapter(targetElementId) {

    override fun getRecipeLayoutId(): Int {
        return R.layout.holder_recipe_plain
    }

    override fun getMachineRecipeLayoutId(): Int {
        return R.layout.holder_recipe_machine_plain
    }

    override fun createViewHolder(itemView: View): RecipeDetailViewHolder {
        return RecipeSelectionViewHolder(itemView)
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

            }
        }
    }
}