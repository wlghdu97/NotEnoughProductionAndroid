package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.oredict.Replacement

internal class OreDictRepoImpl constructor(private val adder: ReplacementAdder) : OreDictRepo {

    override suspend fun insertReplacements(list: List<Replacement>) {
        adder.insertReplacements(list)
    }
}
