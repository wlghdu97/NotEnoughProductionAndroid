package com.xhlab.nep.model.process

import com.xhlab.nep.model.ElementView

data class ElementImpl(
    override val id: Long,
    override val localizedName: String,
    override val unlocalizedName: String,
    override val type: Int,
    override val metaData: String?
) : ElementView() {
    override val amount: Int = 1
}