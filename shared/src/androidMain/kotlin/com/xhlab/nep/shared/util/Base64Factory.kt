package com.xhlab.nep.shared.util

import android.util.Base64

actual object Base64Factory {

    actual val noWrapEncoder: Base64Encoder = AndroidNoWrapEncoder
    actual val noWrapDecoder: Base64Decoder = AndroidNoWrapDecoder

    object AndroidNoWrapEncoder : Base64Encoder {

        override fun encode(src: ByteArray): ByteArray {
            return Base64.encode(src, Base64.NO_WRAP)
        }
    }

    object AndroidNoWrapDecoder : Base64Decoder {

        override fun decode(src: ByteArray): ByteArray {
            return Base64.decode(src, Base64.NO_WRAP)
        }
    }
}
