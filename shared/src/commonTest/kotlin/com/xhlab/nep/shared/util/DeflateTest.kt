package com.xhlab.nep.shared.util

import kotlin.test.Test
import kotlin.test.assertEquals

class DeflateTest {

    @Test
    fun testDeflater() {
        val src = ORIGINAL.encodeToByteArray()
        assertEquals(
            ENCODED, Deflater(src).deflate().decodeToString()
        )
    }

    @Test
    fun testInflater() {
        val src = Deflater(ORIGINAL.encodeToByteArray()).deflate()
        assertEquals(
            ORIGINAL, Inflater(src).inflate().decodeToString()
        )
    }

    companion object {
        private const val ORIGINAL = "string for deflate"
        private const val ENCODED = "+.)��KWH�/RHIM�I,I\u0005\u0000"
    }
}
