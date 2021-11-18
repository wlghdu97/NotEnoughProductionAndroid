package com.xhlab.nep.model

import kotlinx.serialization.Serializable

@Serializable
data class PlainRecipeElement(
    override val id: Long,
    override val unlocalizedName: String,
    override val localizedName: String,
    override val type: Int,
    override val metaData: String? = null,
    override val amount: Int
) : RecipeElement()
