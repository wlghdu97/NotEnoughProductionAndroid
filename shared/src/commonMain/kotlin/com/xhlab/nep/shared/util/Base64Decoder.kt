package com.xhlab.nep.shared.util

interface Base64Decoder {
    fun decode(src: ByteArray): ByteArray
}
