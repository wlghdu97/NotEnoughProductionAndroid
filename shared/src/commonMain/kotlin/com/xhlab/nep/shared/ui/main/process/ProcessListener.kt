package com.xhlab.nep.shared.ui.main.process

interface ProcessListener {
    fun onClick(id: String, name: String)
    fun onRename(id: String, prevName: String)
    fun onExportString(id: String)
    fun onDelete(id: String, name: String)
}
