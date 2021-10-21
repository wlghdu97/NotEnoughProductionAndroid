package com.xhlab.nep.shared.data.element

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.data.generateLongUUID
import com.xhlab.nep.shared.db.entity.ElementType
import com.xhlab.nep.shared.db.Element as ElementEntity

class SqlDelightElementMapper : Mapper<Element, List<ElementEntity>> {

    override fun map(element: Element): List<ElementEntity> {
        return when (element) {
            is OreDictElement -> element.oreDictNameList.map {
                ElementEntity(
                    id = generateLongUUID(),
                    unlocalized_name = it,
                    localized_name = "",
                    type = ElementType.ORE_CHAIN.index
                )
            }
            else -> listOf(
                ElementEntity(
                    id = generateLongUUID(),
                    unlocalized_name = element.unlocalizedName,
                    localized_name = element.localizedName,
                    type = if (element is Fluid) {
                        ElementType.FLUID.index
                    } else {
                        ElementType.ITEM.index
                    }
                )
            )
        }
    }
}
