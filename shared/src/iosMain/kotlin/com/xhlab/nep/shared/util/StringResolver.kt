package com.xhlab.nep.shared.util

import com.xhlab.nep.annotation.OpenForTesting
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.PluralFormatted
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import platform.Foundation.NSString
import platform.Foundation.stringWithFormat

@OpenForTesting
actual class StringResolver {

    actual fun getString(res: StringResource): String {
        return StringDesc.Resource(res).localized()
    }

    actual fun getPluralString(res: PluralsResource, quantity: Int): String {
        return StringDesc.PluralFormatted(res, quantity, quantity).localized()
    }

    // this was from https://stackoverflow.com/a/64499248/13203145
    actual fun formatString(format: StringResource, vararg args: Any): String {
        val formatString = getString(format)
        var returnString = ""
        val regEx = "%[\\d|,.]*[sdf]|[%]".toRegex()
        val singleFormats = regEx.findAll(formatString).map {
            it.groupValues.first()
        }.asSequence().toList()
        val newStrings = formatString.split(regEx)
        for (i in 0 until args.count()) {
            val arg = args[i]
            returnString += when (arg) {
                is Double -> {
                    NSString.stringWithFormat(newStrings[i] + singleFormats[i], args[i] as Double)
                }
                is Int -> {
                    NSString.stringWithFormat(newStrings[i] + singleFormats[i], args[i] as Int)
                }
                else -> {
                    NSString.stringWithFormat(newStrings[i] + "%@", args[i])
                }
            }
        }
        returnString += newStrings.last()

        return returnString
    }
}
