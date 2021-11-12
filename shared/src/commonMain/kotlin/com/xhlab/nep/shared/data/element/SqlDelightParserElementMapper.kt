package com.xhlab.nep.shared.data.element

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.FluidForm
import com.xhlab.nep.model.form.OreDictForm
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.util.UUID
import com.xhlab.nep.shared.db.Element as ElementEntity

class SqlDelightParserElementMapper : Mapper<ElementForm, List<ElementEntity>> {

    override fun map(element: ElementForm): List<ElementEntity> {
        return when (element) {
            is OreDictForm -> element.oreDictNameList.map {
                ElementEntity(
                    id = UUID.generateLongUUID(),
                    unlocalized_name = it,
                    localized_name = "",
                    type = Element.ORE_CHAIN
                )
            }
            else -> listOf(
                ElementEntity(
                    id = UUID.generateLongUUID(),
                    unlocalized_name = element.unlocalizedName,
                    localized_name = element.localizedName,
                    type = if (element is FluidForm) {
                        Element.FLUID
                    } else {
                        Element.ITEM
                    }
                )
            )
        }
    }
}
