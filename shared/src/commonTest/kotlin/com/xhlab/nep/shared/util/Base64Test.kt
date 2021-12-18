package com.xhlab.nep.shared.util

import kotlin.test.Test
import kotlin.test.assertEquals

class Base64Test {

    @Test
    fun testBase64Encoding() {
        val src = ORIGINAL.encodeToByteArray()
        assertEquals(
            ENCODED, Base64Factory.noWrapEncoder.encode(src).decodeToString()
        )
    }

    @Test
    fun testBase64Decoding() {
        val encoded = ENCODED.encodeToByteArray()
        assertEquals(
            ORIGINAL, Base64Factory.noWrapDecoder.decode(encoded).decodeToString()
        )
    }

    companion object {
        private const val ORIGINAL = "string for encoding"
        private const val ENCODED = "c3RyaW5nIGZvciBlbmNvZGluZw=="
    }
}
