package com.xhlab.nep.model

data class PlainRecipeElement(
    override val id: Long,
    override val unlocalizedName: String,
    override val localizedName: String,
    override val type: Int,
    override val metaData: String?,
    override val amount: Int
) : RecipeElement()
