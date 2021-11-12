package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.form.ReplacementForm

internal class OreDictRepoImpl constructor(private val adder: ReplacementAdder) : OreDictRepo {

    override suspend fun insertReplacements(list: List<ReplacementForm>) {
        adder.insertReplacements(list)
    }
}
