package com.xhlab.nep.ui.process.adapters

import androidx.recyclerview.widget.DiffUtil
import com.xhlab.nep.model.RecipeElement

fun getRecipeElementDiffer(
    oldList: List<RecipeElement>,
    newList: List<RecipeElement>
) = object : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size
}
