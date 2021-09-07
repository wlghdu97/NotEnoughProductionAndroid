package com.xhlab.nep.model.oredict

import com.xhlab.nep.model.Element

data class OreDictElement(
    override val amount: Int,
    val oreDictNameList: List<String>
) : Element() {
    override val unlocalizedName: String = oreDictNameList.toString()
    override val localizedName: String = ""
}
