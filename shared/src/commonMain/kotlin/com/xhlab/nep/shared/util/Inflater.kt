package com.xhlab.nep.shared.util

expect class Inflater constructor(input: ByteArray) {
    fun inflate(): ByteArray
}
