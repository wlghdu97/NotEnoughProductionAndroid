package com.xhlab.nep.ui.process.adapters

import com.xhlab.nep.model.RecipeElement
import com.xhlab.nep.model.process.Process

data class ElementConnection(
    val element: RecipeElement,
    val connections: List<Process.Connection>
) : RecipeElement() {
    override val id: Long
        get() = element.id
    override val localizedName: String
        get() = element.localizedName
    override val unlocalizedName: String
        get() = element.unlocalizedName
    override val type: Int
        get() = element.type
    override val metaData: String?
        get() = element.metaData
    override val amount: Int
        get() = element.amount
}
