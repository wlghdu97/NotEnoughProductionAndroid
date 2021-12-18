package com.xhlab.nep.shared.util

import java.util.UUID

internal actual object UUID {

    actual fun generateLongUUID(): Long {
        return UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE
    }

    actual fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}
