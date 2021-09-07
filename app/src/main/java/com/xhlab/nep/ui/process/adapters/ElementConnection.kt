package com.xhlab.nep.ui.process.adapters

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process

data class ElementConnection(
    val element: Element,
    val connections: List<Process.Connection>
) : Element() {
    override val amount: Int
        get() = element.amount
    override val localizedName: String
        get() = element.localizedName
    override val unlocalizedName: String
        get() = element.unlocalizedName
}
