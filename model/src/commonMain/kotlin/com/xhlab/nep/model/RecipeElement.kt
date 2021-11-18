package com.xhlab.nep.model

import kotlinx.serialization.Serializable

@Serializable
abstract class RecipeElement : Element() {
    abstract val id: Long
    abstract override val unlocalizedName: String
    abstract override val localizedName: String
    abstract val type: Int
    abstract val metaData: String?
    abstract val amount: Int
}
