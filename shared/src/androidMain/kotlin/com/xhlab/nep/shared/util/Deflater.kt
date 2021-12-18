package com.xhlab.nep.shared.util

import java.io.ByteArrayOutputStream
import java.util.zip.Deflater

actual class Deflater actual constructor(private val input: ByteArray) {

    private val deflater: Deflater
        get() = Deflater(Deflater.DEFAULT_COMPRESSION, true).apply {
            setInput(input)
        }

    actual fun deflate(): ByteArray = with(deflater) {
        ByteArrayOutputStream().use {
            finish()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (!finished()) {
                val length = deflate(buffer)
                if (length == 0) {
                    break
                }
                it.write(buffer, 0, length)
            }
            end()
            it.toByteArray()
        }
    }
}
