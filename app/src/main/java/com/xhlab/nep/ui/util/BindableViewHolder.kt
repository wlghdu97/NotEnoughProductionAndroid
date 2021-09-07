package com.xhlab.nep.ui.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var model: T? = null

    fun bind(model: T?) {
        this.model = model
        if (model != null) {
            bindNotNull(model)
        }
    }

    abstract fun bindNotNull(model: T)
}
