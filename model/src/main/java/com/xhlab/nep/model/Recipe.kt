package com.xhlab.nep.model

import java.io.Serializable

interface Recipe : Serializable {
    fun getInputs(): List<Element>
    fun getOutput(): List<Element>
}
