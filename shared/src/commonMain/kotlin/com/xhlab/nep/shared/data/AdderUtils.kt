package com.xhlab.nep.shared.data

import com.xhlab.nep.model.form.ElementForm
import com.xhlab.nep.model.form.OreDictForm
import com.xhlab.nep.shared.db.Nep

internal fun ElementForm.getId(db: Nep): Long {
    return when (this) {
        is OreDictForm -> {
            db.elementQueries.getOreDictChainIds(oreDictNameList).executeAsList().first()
        }
        else -> {
            db.elementQueries.getIds(unlocalizedName).executeAsList().first()
        }
    }
}
