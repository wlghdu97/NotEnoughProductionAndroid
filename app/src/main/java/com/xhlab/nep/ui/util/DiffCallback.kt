package com.xhlab.nep.ui.util

import androidx.recyclerview.widget.DiffUtil

abstract class DiffCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>
) : DiffUtil.Callback() {

    abstract override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun getNewListSize() = newList.size

    override fun getOldListSize() = oldList.size
}