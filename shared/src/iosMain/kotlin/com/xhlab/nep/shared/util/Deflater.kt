package com.xhlab.nep.shared.util

import kotlinx.cinterop.memScoped
import platform.Foundation.NSDataCompressionAlgorithmZlib
import platform.Foundation.compressedDataUsingAlgorithm

actual class Deflater actual constructor(private val input: ByteArray) {

    actual fun deflate(): ByteArray = memScoped {
        return input.toNSData()
            .compressedDataUsingAlgorithm(NSDataCompressionAlgorithmZlib, null)
            ?.toByteArray() ?: throw NSDataCompressionException()
    }
}
