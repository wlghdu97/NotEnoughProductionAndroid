package com.xhlab.nep.ui.main.process.creator.browser.details

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.shared.ui.main.process.creator.browser.details.RootRecipeSelectionListener
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.adapters.RecipeDetailViewHolder
import com.xhlab.nep.ui.process.editor.selection.outer.details.RecipeSelectionElementAdapter

class RecipeSelectionAdapter(
    targetElementId: Long?,
    private val selectionListener: RootRecipeSelectionListener? = null
) : RecipeDetailAdapter(targetElementId) {

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
                selectionListener?.let { listener ->
                    val targetRecipe = model
                    if (targetRecipe != null) {
                        val elementList = targetRecipe.itemList + targetRecipe.resultItemList
                        val keyElement = elementList.find { it.id == targetElementId }
                        if (keyElement != null) {
                            listener.onSelect(targetRecipe, keyElement)
                        }
                    }
                }
            }
        }
    }
}
