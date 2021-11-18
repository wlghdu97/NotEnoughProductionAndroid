package com.xhlab.nep.shared.util

expect object Base64Factory {
    val noWrapEncoder: Base64Encoder
    val noWrapDecoder: Base64Decoder
}
