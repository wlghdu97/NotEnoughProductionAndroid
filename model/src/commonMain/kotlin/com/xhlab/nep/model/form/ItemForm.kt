package com.xhlab.nep.model.form

data class ItemForm(
    override val amount: Int = 0,
    override val unlocalizedName: String,
    override val localizedName: String = "",
    val metaData: String? = null
) : ElementForm()
