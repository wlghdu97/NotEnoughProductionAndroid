package com.xhlab.nep.ui.process.adapters

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.process.Process

data class ElementConnection(
    override val amount: Int,
    override val localizedName: String,
    override val unlocalizedName: String,
    val connections: List<Process.Connection>
) : Element()