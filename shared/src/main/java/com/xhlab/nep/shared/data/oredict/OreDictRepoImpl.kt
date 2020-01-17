package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.oredict.Replacement
import javax.inject.Inject

internal class OreDictRepoImpl @Inject constructor(
    private val adder: ReplacementAdder
) : OreDictRepo {

    override suspend fun insertReplacements(list: List<Replacement>) {
        adder.insertReplacements(list)
    }
}