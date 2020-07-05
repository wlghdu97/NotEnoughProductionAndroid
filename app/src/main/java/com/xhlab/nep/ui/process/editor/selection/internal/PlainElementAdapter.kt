package com.xhlab.nep.ui.process.editor.selection.internal

import android.view.View
import com.xhlab.nep.R
import com.xhlab.nep.ui.process.adapters.ProcessElementAdapter
import com.xhlab.nep.ui.process.adapters.ProcessElementViewHolder

class PlainElementAdapter : ProcessElementAdapter() {

    override fun getElementHolderLayoutId(): Int {
        return R.layout.holder_element
    }

    override fun getMultiElementHolderLayoutId(): Int {
        return R.layout.holder_process_element_multi_plain
    }

    override fun onCreateElementHolder(itemView: View): ProcessElementViewHolder {
        return PlainElementViewHolder(itemView)
    }

    inner class PlainElementViewHolder(itemView: View) : ProcessElementViewHolder(itemView) {
        override val isIconVisible: Boolean
            get() = this@PlainElementAdapter.isIconVisible
        override val showConnection: Boolean
            get() = this@PlainElementAdapter.showConnection

        init {
            with (itemView) {
                background = null
                isClickable = false
                isFocusable = false
            }
        }
    }
}