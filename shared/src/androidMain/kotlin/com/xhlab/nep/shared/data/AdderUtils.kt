package com.xhlab.nep.shared.data

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.db.Nep
import java.util.*

internal fun generateLongUUID() = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

internal fun Element.getId(db: Nep): Long {
    return when (this) {
        is OreDictElement -> {
            db.elementQueries.getOreDictChainIds(oreDictNameList).executeAsList().first()
        }
        else -> {
            db.elementQueries.getIds(unlocalizedName).executeAsList().first()
        }
    }
}
