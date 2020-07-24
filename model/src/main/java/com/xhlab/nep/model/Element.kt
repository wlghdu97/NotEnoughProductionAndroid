package com.xhlab.nep.model

import java.io.Serializable

abstract class Element : Serializable {
    abstract val amount: Int
    abstract val unlocalizedName: String
    abstract val localizedName: String
}