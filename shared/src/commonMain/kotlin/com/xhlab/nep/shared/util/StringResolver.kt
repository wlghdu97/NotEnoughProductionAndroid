package com.xhlab.nep.shared.util

import com.xhlab.nep.annotation.OpenForTesting
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource

@OpenForTesting
expect class StringResolver {
    fun getString(res: StringResource): String
    fun getPluralString(res: PluralsResource, quantity: Int): String
    fun formatString(format: StringResource, vararg args: Any): String
}
