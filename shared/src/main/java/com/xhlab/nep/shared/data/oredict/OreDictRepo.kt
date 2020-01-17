package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.oredict.Replacement

interface OreDictRepo {
    suspend fun insertReplacements(list: List<Replacement>)
}