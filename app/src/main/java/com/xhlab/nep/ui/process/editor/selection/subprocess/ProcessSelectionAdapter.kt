package com.xhlab.nep.ui.process.editor.selection.subprocess

import com.xhlab.nep.R
import com.xhlab.nep.shared.ui.main.process.ProcessListener
import com.xhlab.nep.ui.adapters.ProcessAdapter

class ProcessSelectionAdapter(listener: ProcessListener? = null) : ProcessAdapter(listener) {

    override fun getHolderLayoutId(): Int {
        return R.layout.holder_process_selection
    }
}
