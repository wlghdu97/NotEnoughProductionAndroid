package com.xhlab.nep.model

data class Fluid(
    override val amount: Int,
    override val unlocalizedName: String,
    override val localizedName: String
) : Element()
