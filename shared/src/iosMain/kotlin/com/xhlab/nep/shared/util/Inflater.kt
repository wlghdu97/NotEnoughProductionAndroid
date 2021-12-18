package com.xhlab.nep.shared.util

import kotlinx.cinterop.memScoped
import platform.Foundation.NSDataCompressionAlgorithmZlib
import platform.Foundation.decompressedDataUsingAlgorithm

actual class Inflater actual constructor(private val input: ByteArray) {

    actual fun inflate(): ByteArray = memScoped {
        return input.toNSData()
            .decompressedDataUsingAlgorithm(NSDataCompressionAlgorithmZlib, null)
            ?.toByteArray() ?: throw NSDataCompressionException()
    }
}
