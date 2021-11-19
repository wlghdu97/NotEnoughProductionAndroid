package com.xhlab.nep.shared.util

expect class Deflater constructor(input: ByteArray) {
    fun deflate(): ByteArray
}
