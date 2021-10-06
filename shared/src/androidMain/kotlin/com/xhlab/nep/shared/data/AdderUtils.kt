package com.xhlab.nep.shared.data

import com.xhlab.nep.model.Element
import com.xhlab.nep.model.oredict.OreDictElement
import com.xhlab.nep.shared.db.AppDatabase
import java.util.*

internal fun generateLongUUID() = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE

internal suspend fun Element.getId(db: AppDatabase): Long {
    return when (this) {
        is OreDictElement -> {
            db.getElementDao().getOreDictChainId(oreDictNameList)
        }
        else -> {
            val unlocalizedName = unlocalizedName
            db.getElementDao().getId(unlocalizedName)
        }
    }
}
