package com.xhlab.nep.shared.util

import platform.Foundation.NSUUID

internal actual object UUID {

    /**
     * Generates random long from msb of UUID.
     *
     * @see java.util.UUID.fromString
     */
    actual fun generateLongUUID(): Long {
        val uuidString = generateUUID()

        val components = uuidString.split("-").toMutableList()
        if (components.size != 5) {
            throw IllegalArgumentException("Invalid UUID string: $uuidString")
        }

        var mostSigBits = components[0].toLong(radix = 16)
        mostSigBits = mostSigBits shl 16
        mostSigBits = mostSigBits or components[1].toLong(radix = 16)
        mostSigBits = mostSigBits shl 16
        mostSigBits = mostSigBits or components[2].toLong(radix = 16)

        return mostSigBits
    }

    actual fun generateUUID(): String {
        return NSUUID.UUID().UUIDString().lowercase()
    }
}
