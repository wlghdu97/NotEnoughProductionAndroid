package com.xhlab.nep.model

abstract class ElementView : Element() {
    abstract val id: Long
    abstract override val unlocalizedName: String
    abstract override val localizedName: String
    abstract val type: Int
    abstract val metaData: String?
}