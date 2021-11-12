package com.xhlab.nep.model.form

data class OreDictForm(
    override val amount: Int,
    val oreDictNameList: List<String>
) : ElementForm() {
    override val unlocalizedName: String = oreDictNameList.toString()
    override val localizedName: String = ""
}
