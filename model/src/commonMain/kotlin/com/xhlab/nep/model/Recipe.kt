package com.xhlab.nep.model

interface Recipe {
    fun getInputs(): List<Element>
    fun getOutput(): List<Element>
}
