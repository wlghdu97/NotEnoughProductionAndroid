package com.xhlab.nep.model.oredict

import com.xhlab.nep.model.RecipeElement
import kotlinx.serialization.Serializable

@Serializable
data class OreDictElement(
    override val id: Long,
    override val metaData: String?,
    override val amount: Int,
    val oreDictNameList: List<String>
) : RecipeElement() {
    override val unlocalizedName: String = oreDictNameList.toString()
    override val localizedName: String = ""
    override val type = ORE_DICT
}
