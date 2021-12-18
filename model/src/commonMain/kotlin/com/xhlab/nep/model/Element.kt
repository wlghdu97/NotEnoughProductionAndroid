package com.xhlab.nep.model

import kotlinx.serialization.Serializable

@Serializable
abstract class Element {
    abstract val unlocalizedName: String
    abstract val localizedName: String

    companion object {
        const val ITEM = 0
        const val FLUID = 1
        const val ORE_DICT = 2
        const val ORE_CHAIN = 3
    }
}
