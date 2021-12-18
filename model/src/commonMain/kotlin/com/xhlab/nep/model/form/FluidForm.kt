package com.xhlab.nep.model.form

data class FluidForm(
    override val amount: Int,
    override val unlocalizedName: String,
    override val localizedName: String
) : ElementForm()
