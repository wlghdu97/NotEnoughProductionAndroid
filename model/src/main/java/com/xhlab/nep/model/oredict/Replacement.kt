package com.xhlab.nep.model.oredict

import com.xhlab.nep.model.Element

data class Replacement(
    val oreDictName: String,
    val elementList: List<Element>
)