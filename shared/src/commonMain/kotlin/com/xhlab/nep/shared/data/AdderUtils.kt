package com.xhlab.nep.shared.data

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.db.Nep

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
