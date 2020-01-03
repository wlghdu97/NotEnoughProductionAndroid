package com.xhlab.nep.shared.data.element

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.Fluid
import com.xhlab.nep.model.Item
import com.xhlab.nep.shared.data.Mapper
import com.xhlab.nep.shared.db.entity.ElementEntity
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.FLUID
import com.xhlab.nep.shared.db.entity.ElementEntity.Companion.ITEM
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomElementMapper @Inject constructor() : Mapper<Element, ElementEntity> {

    override fun map(element: Element): ElementEntity {
        return ElementEntity(
            unlocalizedName = element.unlocalizedName,
            localizedName = element.localizedName,
            type = if (element is Fluid) FLUID else ITEM,
            metaData = if (element is Item) element.metaData?.toString() ?: "" else ""
        )
    }
}