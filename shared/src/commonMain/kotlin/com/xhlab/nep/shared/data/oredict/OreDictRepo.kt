package com.xhlab.nep.shared.data.oredict

import com.xhlab.nep.model.form.ReplacementForm

interface OreDictRepo {
    suspend fun insertReplacements(list: List<ReplacementForm>)
}
