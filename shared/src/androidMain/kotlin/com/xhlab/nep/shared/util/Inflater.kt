package com.xhlab.nep.shared.util

import java.io.ByteArrayOutputStream
import java.util.zip.Inflater

actual class Inflater actual constructor(private val input: ByteArray) {

    private val inflater: Inflater
        get() = Inflater(true).apply {
            setInput(input)
        }

    actual fun inflate(): ByteArray = with(inflater) {
        ByteArrayOutputStream().use {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (!finished()) {
                val length = inflate(buffer)
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
