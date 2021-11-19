package com.xhlab.nep.shared.util

import kotlinx.cinterop.memScoped
import platform.Foundation.NSData
import platform.Foundation.base64EncodedDataWithOptions
import platform.Foundation.create

actual object Base64Factory {

    actual val noWrapEncoder: Base64Encoder = NativeNoWrapEncoder
    actual val noWrapDecoder: Base64Decoder = NativeNoWrapDecoder

    object NativeNoWrapEncoder : Base64Encoder {

        override fun encode(src: ByteArray): ByteArray {
            val data = src.toNSData()
            val encoded = data.base64EncodedDataWithOptions(0)
            return encoded.toByteArray()
        }
    }

    object NativeNoWrapDecoder : Base64Decoder {

        override fun decode(src: ByteArray): ByteArray {
            val decoded = memScoped {
                NSData.create(base64EncodedData = src.toNSData(), options = 0)
            } ?: throw RuntimeException("Failed to decode base64 data : $src")
            return decoded.toByteArray()
        }
    }
}
