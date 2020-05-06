package com.xhlab.nep.shared.domain.item.model

abstract class ElementView {
    abstract val id: Long
    abstract val unlocalizedName: String
    abstract val localizedName: String
    abstract val type: Int
    abstract val metaData: String?
}