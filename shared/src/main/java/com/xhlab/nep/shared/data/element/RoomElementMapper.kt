package com.xhlab.nep.shared.data.element

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.FLUID
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ORE_DICT
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomElementMapper @Inject constructor() : Mapper<Element, List<ElementEntity>> {

    override fun map(element: Element): List<ElementEntity> {
        return when (element) {
            is OreDictElement -> element.oreDictNameList.map {
                ElementEntity(
                    unlocalizedName = it,
                    localizedName = "",
                    type = ORE_DICT
                )
            }
            else -> listOf(
                ElementEntity(
                    unlocalizedName = element.unlocalizedName,
                    localizedName = element.localizedName,
                    type = if (element is Fluid) FLUID else ITEM,
                    metaData = if (element is Item) element.metaData?.toString() ?: "" else ""
                )
            )
        }
    }
}