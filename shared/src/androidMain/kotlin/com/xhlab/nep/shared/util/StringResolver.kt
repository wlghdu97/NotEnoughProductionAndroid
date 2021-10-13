package com.xhlab.nep.shared.util

import android.content.Context
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.Plural
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc

actual class StringResolver constructor(private val context: Context) {

    actual fun getString(res: StringResource): String {
        return StringDesc.Resource(res).toString(context)
    }

    actual fun getPluralString(res: PluralsResource, quantity: Int): String {
        return StringDesc.Plural(res, quantity).toString()
    }

    actual fun formatString(format: StringResource, vararg args: Any): String {
        return StringDesc.ResourceFormatted(format, *args).toString(context)
    }
}

