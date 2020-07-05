package com.xhlab.nep.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.R
import com.xhlab.nep.model.recipes.view.CraftingRecipeView
import com.xhlab.nep.model.recipes.view.MachineRecipeView
import com.xhlab.nep.model.recipes.view.RecipeView
import com.xhlab.nep.ui.main.items.ElementListener

open class RecipeDetailAdapter(
    protected val targetElementId: Long? = null,
    protected val listener: ElementListener? = null
) : PagedListAdapter<RecipeView, RecipeDetailViewHolder>(Differ) {

    protected var isIconVisible = false

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeDetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(when (viewType) {
                0 -> getRecipeLayoutId()
                1 -> getMachineRecipeLayoutId()
                else -> throw IllegalArgumentException("invalid view type.")
            }, parent, false)
        return createViewHolder(view)
    }

    protected open fun getRecipeLayoutId(): Int = R.layout.holder_recipe

    protected open fun getMachineRecipeLayoutId(): Int = R.layout.holder_recipe_machine

    open fun createViewHolder(itemView: View): RecipeDetailViewHolder {
        return DefaultRecipeDetailViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    final override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CraftingRecipeView -> 0
            is MachineRecipeView -> 1
            else -> throw IllegalArgumentException("invalid recipe view type.")
        }
    }

    fun setIconVisibility(isVisible: Boolean) {
        isIconVisible = isVisible
        notifyDataSetChanged()
    }

    inner class DefaultRecipeDetailViewHolder(itemView: View) : RecipeDetailViewHolder(itemView) {
        override val itemAdapter = RecipeElementAdapter(listener)
        override val byproductAdapter = RecipeElementAdapter(listener)

        override val targetElementId: Long?
            get() = this@RecipeDetailAdapter.targetElementId
        override val isIconVisible: Boolean
            get() = this@RecipeDetailAdapter.isIconVisible
    }

    private object Differ : DiffUtil.ItemCallback<RecipeView>() {
        override fun areItemsTheSame(oldItem: RecipeView, newItem: RecipeView): Boolean {
            return oldItem.recipeId == newItem.recipeId
        }

        override fun areContentsTheSame(oldItem: RecipeView, newItem: RecipeView): Boolean {
            return (oldItem.recipeId == newItem.recipeId &&
                    oldItem.itemList == oldItem.itemList)
        }
    }
}