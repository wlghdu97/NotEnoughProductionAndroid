package com.xhlab.nep.shared.util

internal expect object UUID {
    fun generateLongUUID(): Long
}
