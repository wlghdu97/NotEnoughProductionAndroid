package com.xhlab.nep.ui.process.editor.selection.outer.details

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.model.Element
import com.xhlab.nep.model.ElementView
import com.xhlab.nep.model.Recipe
import com.xhlab.nep.model.process.OreChainRecipe
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_CHAIN
import com.xhlab.nep.ui.adapters.RecipeDetailAdapter
import com.xhlab.nep.ui.adapters.RecipeDetailViewHolder
import com.xhlab.nep.ui.process.editor.ProcessEditViewModel
import com.xhlab.nep.ui.process.editor.selection.RecipeSelectionListener
import com.xhlab.nep.ui.process.editor.selection.getKeyElement
import com.xhlab.nep.ui.process.editor.selection.isReversed
import com.xhlab.nep.ui.process.editor.selection.outer.OreDictRecipeSelectionListener

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
                if (constraint != null && node != null) {
                    val connectToParent = constraint.connectToParent
                    val reversed = constraint.isReversed()
                    val from = constraint.recipe
                    val to = node
                    val element = constraint.getKeyElement(from)
                    if (element != null) {
                        if (constraint.recipe !is OreChainRecipe &&
                            constraint.element.type == ORE_CHAIN) {
                            val ingredient = getElement(to, targetElementId!!)
                            if (ingredient != null) {
                                when (connectToParent xor reversed) {
                                    true -> oreDictSelectionListener?.onSelectOreDict(from, to, element, ingredient, reversed)
                                    false -> oreDictSelectionListener?.onSelectOreDict(to, from, element, ingredient, reversed)
                                }
                            }
                        } else {
                            when (connectToParent xor reversed) {
                                true -> selectionListener?.onSelect(from, to, element, reversed)
                                false -> selectionListener?.onSelect(to, from, element, reversed)
                            }
                        }
                    }
                }
            }
        }

        private fun getElement(recipe: Recipe, elementId: Long): Element? {
            return (recipe.getInputs() + recipe.getOutput()).find {
                (it as? ElementView)?.id == elementId
            }
        }
    }
}