package com.xhlab.nep.model

data class Item(
    override val amount: Int = 0,
    override val unlocalizedName: String,
    override val localizedName: String = "",
    val metaData: Int? = null
) : Element()